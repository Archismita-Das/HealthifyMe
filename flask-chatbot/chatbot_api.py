# ============================================
# HealthifyMe Intelligent Chatbot API (FIXED VERSION)
# ============================================
from flask import Flask, request, jsonify
import spacy
import mysql.connector
from mysql.connector import Error
import re
from datetime import datetime
import difflib
import random
import os

# ============================================
# INITIALIZE FLASK APP & LOAD SPACY MODEL
# ============================================
app = Flask(__name__)
try:
    nlp = spacy.load("en_core_web_sm")
    print("✅ spaCy model loaded successfully")
except OSError:
    print("❌ spaCy model not found. Run: python -m spacy download en_core_web_sm")
    nlp = None

# ============================================
# DATABASE CONFIGURATION
# ============================================
DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "database": "fitness_db",
    "user": "root",
    "password": "Anindita@2005"
}


# ============================================
# GLOBAL CONTEXT MANAGER
# ============================================
class ChatContext:
    def __init__(self):
        self.context = {}
        self.last_entities = {}
    
    def save_context(self, intent, entities):
        self.context['last_intent'] = intent
        self.last_entities = entities
    
    def get_last_context(self):
        return self.context
    
    def get_last_entities(self):
        return self.last_entities
    
    def update_context(self, **kwargs):
        self.context.update(kwargs)

context_manager = ChatContext()

# ============================================
# DATABASE CONNECTION FUNCTION
# ============================================
def get_db_connection():
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        if connection.is_connected():
            return connection
    except Error as e:
        print(f"❌ Database connection error: {e}")
        return None

# ============================================
# RESPONSE TEMPLATES
# ============================================
def choose_random(category):
    responses = {
        "greeting": [
            "Hello! I'm your HealthifyMe assistant. How can I help you today?",
            "Hi there! I'm here to help with your nutrition and fitness questions.",
            "Good day! What can I help you with regarding your health goals?"
        ],
        "goodbye": [
            "Goodbye! Stay healthy and come back anytime!",
            "See you later! Remember to maintain a balanced diet.",
            "Bye! Keep up the good work on your health journey!"
        ],
        "diet_tip": [
            "🥑 **General Diet Tip:**\n\nInclude all macros - carbs for energy, protein for muscle, healthy fats for hormones and satiety.",
            "🥑 **General Diet Tip:**\n\nFocus on whole foods, limit processed items, and eat a variety of fruits and vegetables.",
            "🥑 **General Diet Tip:**\n\nStay hydrated and aim for a colorful plate with different types of foods."
        ],
        "hydration": [
            "💧 **Hydration Tip:**\n\nDrink at least 8 glasses of water per day. Water helps maintain body temperature, lubricates joints, and helps transport nutrients.",
            "💧 **Hydration Tip:**\n\nYour body needs about 2-3 liters of water per day. Increase intake during exercise or hot weather.",
            "💧 **Hydration Tip:**\n\nProper hydration improves brain function, energy levels, and physical performance. Keep a water bottle with you throughout the day."
        ],
        "bmi_bmr": [
            "📊 **BMI & BMR Information:**\n\nBMI measures body fat based on height and weight. BMR is the number of calories your body needs at rest.",
            "📊 **BMI & BMR Information:**\n\nBMI helps determine if you're at a healthy weight. BMR helps estimate daily calorie needs.",
            "📊 **BMI & BMR Information:**\n\nBMI ranges: Underweight (<18.5), Normal (18.5-24.9), Overweight (25-29.9), Obese (≥30)."
        ],
        "fallback": [
            "I'm not sure how to respond to that. I can help with nutrition information, food recommendations, diet tips, and exercise advice.",
            "I don't understand. Could you please rephrase your question?",
            "I'm here to help with health and nutrition questions. Could you try asking in a different way?"
        ]
    }
    
    return random.choice(responses.get(category, ["I'm not sure how to respond to that."]))

# ============================================
# INTENT CLASSIFIER (FIXED)
# ============================================
def get_intent(message):
    message = message.lower()
    
    # Priority-based intent detection
    if any(word in message for word in ["calories in", "how many calories", "nutrition in", "protein in", "carbs in", "fats in"]):
        return "food_query"
    elif any(word in message for word in ["suggest", "recommend", "give me", "list of", "what are some", "show me", "options"]):
        return "food_recommendation"
    elif any(word in message for word in ["more", "else", "another", "what else", "give me more"]):
        return "context_followup"
    elif any(word in message for word in ["hello", "hi", "hey", "good morning", "good evening"]):
        return "greeting"
    elif any(word in message for word in ["goodbye", "bye", "see you", "take care"]):
        return "goodbye"
    elif any(word in message for word in ["diet", "healthy", "nutrition advice", "eating healthy"]):
        return "diet_tip"
    elif any(word in message for word in ["water", "hydration", "drink", "how much water"]):
        return "hydration"
    elif any(word in message for word in ["bmi", "bmr", "body mass index", "calculate bmi"]):
        return "bmi_bmr"
    elif any(word in message for word in ["exercise", "workout", "fitness", "training", "gym"]):
        return "exercise"
    elif any(word in message for word in ["weight loss", "lose weight", "burn fat"]):
        return "exercise"  # Map weight loss to exercise intent
    elif any(word in message for word in ["weight gain", "gain weight", "build muscle"]):
        return "exercise"  # Map weight gain to exercise intent
    elif any(word in message for word in ["how", "what", "why", "when", "which"]):
        return "faq"
    # NEW: Handle simple meal type requests
    elif any(word in message for word in ["breakfast", "lunch", "dinner", "snack"]):
        return "food_recommendation"
    else:
        return "fallback"

# ============================================
# SAFE SPELLING CORRECTION FUNCTION
# ============================================
def get_all_food_names_from_db():
    connection = get_db_connection()
    if not connection: 
        return []
    
    food_names = []
    try:
        cursor = connection.cursor()
        cursor.execute("SELECT food_name FROM foods")
        results = cursor.fetchall()
        food_names = [row[0].lower() for row in results]
        cursor.close()
        connection.close()
    except Error as e:
        print(f"❌ Error fetching food names: {e}")
        return []
    
    return food_names

FOOD_NAME_DICTIONARY = get_all_food_names_from_db()

def correct_food_spelling(text):
    words = text.lower().split()
    corrected_words = []
    for word in words:
        close_matches = difflib.get_close_matches(word, FOOD_NAME_DICTIONARY, n=1, cutoff=0.7)
        if close_matches:
            corrected_word = close_matches[0]
            if corrected_word != word:
                print(f"🔧 Food spelling corrected: '{word}' -> '{corrected_word}'")
            corrected_words.append(corrected_word)
        else:
            corrected_words.append(word)
    return " ".join(corrected_words)

# ============================================
# MANUAL LUNCH FOODS LIST
# ============================================
def get_manual_lunch_foods():
    """Return a list of known lunch foods that should be prioritized"""
    return [
        "Dal", "Rajma", "Chana Masala", "Sambhar", "Vegetable Curry", "Mixed Veg",
        "Aloo Gobi", "Aloo Matar", "Palak Paneer", "Mushroom Masala", "Baingan Bharta",
        "Biryani", "Pulao", "Khichdi", "Fried Rice", "Jeera Rice", "Ghee Rice",
        "Roti", "Naan", "Paratha", "Chapati", "Kulcha", "Poori",
        "Thali", "Combo", "Platter", "Special", "Meal", "Bowl"
    ]

# ============================================
# NAMED ENTITY RECOGNITION FUNCTION (FIXED)
# ============================================
def extract_entities(text):
    entities = {
        "foods": [], 
        "numbers": [], 
        "dietary_restrictions": [], 
        "meal_types": [], 
        "nutrients": [], 
        "qualifiers": [],
        "cuisines": []
    }
    
    # Extract dietary restrictions - IMPROVED LOGIC
    restrictions = ['vegan', 'vegetarian', 'gluten-free', 'dairy-free', 'low-carb', 'keto', 'paleo', 'non-veg', 'non veg']
    for restriction in restrictions:
        if restriction in text.lower():
            entities["dietary_restrictions"].append(restriction)
    
    # Extract meal types - IMPROVED LOGIC
    meal_types = ['breakfast', 'lunch', 'dinner', 'snack', 'brunch']
    for meal_type in meal_types:
        if meal_type in text.lower():
            entities["meal_types"].append(meal_type)
    
    # Extract cuisines - IMPROVED LOGIC
    cuisines = ['indian', 'global', 'western', 'chinese', 'mexican', 'italian', 'thai']
    for cuisine in cuisines:
        if cuisine in text.lower():
            entities["cuisines"].append(cuisine)
    
    # Extract nutrients - IMPROVED LOGIC
    nutrients = ['calories', 'protein', 'carbs', 'carbohydrates', 'fats', 'fiber', 'sugar', 'sodium']
    for nutrient in nutrients:
        if nutrient in text.lower():
            entities["nutrients"].append(nutrient)
    
    # Extract qualifiers - IMPROVED LOGIC
    qualifiers = ['high', 'low', 'rich in', 'free of', 'healthy', 'unhealthy']
    for qualifier in qualifiers:
        if qualifier in text.lower():
            entities["qualifiers"].append(qualifier)
    
    # Extract numbers - IMPROVED LOGIC
    numbers_in_text = re.findall(r'\b\d+(?:\.\d+)?\b', text)
    for num in numbers_in_text:
        num_float = float(num)
        if num_float not in entities["numbers"]:
            entities["numbers"].append(num_float)
    
    # Extract food items using spaCy if available
    if nlp is not None:
        non_food_words = {
            "calorie", "calories", "nutrition", "protein", "protien", "carbs", "carbohydrate", "carbohydrates",
            "fat", "fats", "gram", "grams", "kcal", "much", "many", "information", "info",
            "food", "guide", "options", "suggest", "recommend", "give", "me", "list", "show"
        }
        
        doc = nlp(text)
        for token in doc:
            if token.pos_ in ["NOUN", "PROPN"] and len(token.text) > 2:
                food_name = token.text.lower()
                # Check if any non-food word is part of the food name
                is_non_food = any(non_food in food_name for non_food in non_food_words)
                if not is_non_food and food_name not in entities["foods"]:
                    entities["foods"].append(food_name)
    else:
        print("⚠️  spaCy not loaded, using basic keyword extraction")
        words = text.lower().split()
        # Filter out words that are likely not food items
        non_food_words = {
            "calorie", "calories", "nutrition", "protein", "protien", "carbs", "carbohydrate", "carbohydrates",
            "fat", "fats", "gram", "grams", "kcal", "much", "many", "information", "info",
            "food", "guide", "options", "suggest", "recommend", "give", "me", "list", "show",
            "hello", "hi", "hey", "goodbye", "bye", "thanks", "thank", "you"
        }
        for w in words:
            if len(w) > 3 and w not in non_food_words and w not in entities["foods"]:
                entities["foods"].append(w)
    
    print(f"🔍 Extracted entities: {entities}")
    return entities

# ============================================
# DATABASE QUERY FUNCTIONS (ENHANCED WITH MEAL TYPE MATCHING)
# ============================================
def query_food_by_name(food_name):
    connection = get_db_connection()
    if not connection: 
        return None
    
    try:
        cursor = connection.cursor(dictionary=True)
        query = "SELECT food_name, calories, protein, carbs, fats, type, cuisine, diet_category, description FROM foods WHERE LOWER(food_name) LIKE %s LIMIT 1"
        cursor.execute(query, (f"%{food_name.lower()}%",))
        result = cursor.fetchone()
        cursor.close()
        connection.close()
        return result
    except Error as e:
        print(f"❌ Database query error: {e}")
        return None

def get_food_fallbacks(food_name, limit=3):
    connection = get_db_connection()
    if not connection: 
        return []
    
    try:
        cursor = connection.cursor(dictionary=True)
        query = "SELECT food_name, calories, type, cuisine FROM foods ORDER BY RAND() LIMIT %s"
        cursor.execute(query, (limit,))
        results = cursor.fetchall()
        cursor.close()
        connection.close()
        return results
    except Error as e:
        print(f"❌ Fallback query error: {e}")
        return []

def query_foods_by_filters(cuisine=None, diet_category=None, max_calories=None, 
                          min_protein=None, meal_type=None, limit=5, offset=0, order_by=None):
    connection = get_db_connection()
    if not connection: 
        return []
    
    try:
        cursor = connection.cursor(dictionary=True)
        query = "SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods WHERE 1=1"
        params = []
        
        if cuisine: 
            query += " AND LOWER(cuisine) = %s"; 
            params.append(cuisine.lower())
            print(f"🔍 Adding cuisine filter: {cuisine}")
        if diet_category: 
            query += " AND LOWER(diet_category) = %s"; 
            params.append(diet_category.lower())
            print(f"🔍 Adding diet_category filter: {diet_category}")
        if max_calories: 
            query += " AND calories <= %s"; 
            params.append(max_calories)
            print(f"🔍 Adding max_calories filter: {max_calories}")
        if min_protein: 
            query += " AND protein >= %s"; 
            params.append(min_protein)
            print(f"🔍 Adding min_protein filter: {min_protein}")
        if meal_type:
            # IMPROVED: Try to match meal_type in food_name, type, or description
            query += " AND (LOWER(type) LIKE %s OR LOWER(food_name) LIKE %s OR LOWER(description) LIKE %s)"
            params.extend([f"%{meal_type.lower()}%", f"%{meal_type.lower()}%", f"%{meal_type.lower()}%"])
            print(f"🔍 Adding meal_type filter: {meal_type}")
        
        if order_by:
            query += f" ORDER BY {order_by}"
        else:
            query += " ORDER BY food_name"
            
        query += " LIMIT %s OFFSET %s"
        params.extend([limit, offset])
        
        print(f"🔍 Final query: {query}")
        print(f"🔍 Query params: {params}")
        
        cursor.execute(query, tuple(params))
        results = cursor.fetchall()
        print(f"🔍 Query returned {len(results)} results")
        cursor.close()
        connection.close()
        return results
    except Error as e:
        print(f"❌ Filter query error: {e}")
        return []

# ============================================
# CHAT HISTORY FUNCTIONS
# ============================================
def save_chat_to_db(user_id, session_id, message, reply, intent):
    """
    Saves a single chat interaction to the database.
    
    Args:
        user_id (str): Identifier for the user.
        session_id (str): Identifier for the chat session.
        message (str): The user's message.
        reply (str): The chatbot's reply.
        intent (str): The detected intent.
    """
    connection = get_db_connection()
    if not connection:
        print("⚠️ Could not save chat to DB: No connection")
        return
    
    try:
        cursor = connection.cursor()
        query = "INSERT INTO chat_history (user_id, session_id, message, reply, intent) VALUES (%s, %s, %s, %s, %s)"
        cursor.execute(query, (user_id, session_id, message, reply, intent))
        connection.commit()
        cursor.close()
        connection.close()
        print("💾 Chat saved to database.")
    except Error as e:
        print(f"❌ Error saving chat to DB: {e}")

def get_user_chat_history(user_id, limit=10):
    """
    Retrieves the last 'n' chat messages for a given user.
    
    Args:
        user_id (str): The user's identifier.
        limit (int): The maximum number of past messages to retrieve.
        
    Returns:
        list: A list of dictionaries, each representing a past chat interaction.
    """
    connection = get_db_connection()
    if not connection: 
        return []
    
    try:
        cursor = connection.cursor(dictionary=True)
        query = "SELECT message, reply, timestamp FROM chat_history WHERE user_id = %s ORDER BY timestamp DESC LIMIT %s"
        cursor.execute(query, (user_id, limit))
        results = cursor.fetchall()
        cursor.close()
        connection.close()
        return results
    except Error as e:
        print(f"❌ Error fetching chat history: {e}")
        return []

# ============================================
# MAIN CHAT ENDPOINT (UPDATED WITH IMPROVED LUNCH AND EXERCISE HANDLING)
# ============================================
@app.route('/chat', methods=['POST'])
def chat():
    try:
        data = request.get_json()
        if not data or 'message' not in data:
            return jsonify({"error": "No message provided", "reply": "Please send a message in the format: {\"message\": \"your text\"}"}), 400
        
        user_message = data['message'].strip()
        if not user_message:
            return jsonify({"intent": "empty", "entities": {}, "reply": "Please type something so I can help you! 😊"})
        
        print(f"\n{'='*50}")
        print(f"📨 Received: {user_message}")
        print(f"{'='*50}")
        
        # User/Session Identification
        user_id = "default_user" 
        session_id = request.headers.get('X-Session-ID', 'default_session')

        corrected_message = correct_food_spelling(user_message)
        intent = get_intent(corrected_message)
        print(f"🎯 Intent detected: {intent}")
        
        entities = extract_entities(corrected_message)
        reply = ""
        
        # GREETING INTENT
        if intent == "greeting":
            reply = choose_random("greeting")
            context_manager.save_context(intent, entities)
        
        # GOODBYE INTENT
        elif intent == "goodbye":
            reply = choose_random("goodbye")
            context_manager.save_context(intent, entities)
        
        # FOOD RECOMMENDATION INTENT (ENHANCED WITH MEAL TYPE LOGIC)
        elif intent == "food_recommendation":
            message_lower = corrected_message.lower()
            cuisine = None
            diet_category = None
            max_calories = None
            min_protein = None
            meal_type = None
            order_by_clause = None
            
            # Extract cuisine from entities or message
            if entities.get("cuisines"):
                cuisine = entities["cuisines"][0].capitalize()
            elif "indian" in message_lower: 
                cuisine = "Indian"
            elif "global" in message_lower or "western" in message_lower: 
                cuisine = "Global"
            elif "chinese" in message_lower: 
                cuisine = "Chinese"
            elif "italian" in message_lower: 
                cuisine = "Italian"
            
            # Extract dietary restrictions from entities or message
            if entities.get("dietary_restrictions"):
                diet_category = entities["dietary_restrictions"][0]
            elif "vegan" in message_lower: 
                diet_category = "vegan"
            elif "vegetarian" in message_lower: 
                diet_category = "vegetarian"
            elif "non-veg" in message_lower or "non veg" in message_lower: 
                diet_category = "non-veg"
            
            # Extract meal type from entities or message
            if entities.get("meal_types"):
                meal_type = entities["meal_types"][0]
            elif "breakfast" in message_lower: 
                meal_type = "breakfast"
            elif "lunch" in message_lower: 
                meal_type = "lunch"
            elif "dinner" in message_lower: 
                meal_type = "dinner"
            elif "snack" in message_lower: 
                meal_type = "snack"
            
            # Extract calorie constraints
            if entities["numbers"]: 
                max_calories = int(entities["numbers"][0])
            elif "low calories" in message_lower or "low calorie" in message_lower:
                max_calories = 150  # Default threshold for low calorie
            
            # Extract protein constraints
            if "high protein" in message_lower or "high in protein" in message_lower:
                min_protein = 10  # Default threshold for high protein
                order_by_clause = "protein DESC"
            
            # Special case for high protein and low calorie
            if ("high in protein" in message_lower or "high protein" in message_lower) and \
               ("less in calories" in message_lower or "low calorie" in message_lower):
                order_by_clause = "protein DESC, calories ASC"
            
            # If no specific criteria were provided, ask for preferences
            if not cuisine and not diet_category and not max_calories and not min_protein and not meal_type:
                reply = "I'd be happy to help with food recommendations! To give you the best suggestions, could you tell me:\n\n"
                reply += "• Any dietary preferences (vegan, vegetarian, non-veg)?\n"
                reply += "• What type of meal (breakfast, lunch, dinner, snack)?\n"
                reply += "• Any cuisine preferences (Indian, Global, etc.)?\n"
                reply += "• Any specific nutritional goals (low calorie, high protein)?\n\n"
                reply += "For example: 'Suggest vegan breakfast options' or 'High protein Indian dinner'"
            else:
                # First try with standard filters
                results = query_foods_by_filters(
                    cuisine, diet_category, max_calories, min_protein, meal_type, 
                    limit=5, offset=0, order_by=order_by_clause
                )
                
                # If no results with standard filters, try special meal type logic
                if not results and diet_category and meal_type:
                    connection = get_db_connection()
                    if connection:
                        try:
                            cursor = connection.cursor(dictionary=True)
                            
                            # For specific meal types, use more targeted queries
                            if meal_type.lower() == "lunch":
                                # Get all foods with the dietary restriction
                                query = "SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods WHERE LOWER(diet_category) = %s ORDER BY RAND() LIMIT 20"
                                cursor.execute(query, (diet_category.lower(),))
                                all_foods = cursor.fetchall()
                                
                                # Get manual lunch foods list
                                manual_lunch_foods = get_manual_lunch_foods()
                                
                                # Filter for lunch-appropriate foods
                                lunch_keywords = [
                                    'curry', 'rice', 'dal', 'sabzi', 'roti', 'wrap', 'sandwich', 'bowl', 'salad', 'meal', 'main', 
                                    'thali', 'biriyani', 'pulao', 'khichdi', 'paratha', 'naan', 'rajma', 'chana', 'masala', 'korma',
                                    'paneer', 'vegetable', 'mix', 'combo', 'platter', 'special', 'dish'
                                ]
                                
                                # Also filter out obvious non-lunch items
                                non_lunch_keywords = [
                                    'butter', 'milk', 'almond', 'cashew', 'walnut', 'oil', 'ghee', 'sugar', 'honey', 'jam',
                                    'sauce', 'spread', 'drink', 'juice', 'shake', 'smoothie'
                                ]
                                
                                lunch_foods = []
                                other_foods = []
                                
                                for food in all_foods:
                                    food_name_lower = food['food_name'].lower()
                                    food_type_lower = food['type'].lower() if food['type'] else ""
                                    
                                    # Check if it's a known lunch food
                                    is_known_lunch = any(lunch_food.lower() in food_name_lower for lunch_food in manual_lunch_foods)
                                    
                                    # Check if food name or type contains lunch keywords
                                    is_lunch_food = is_known_lunch or \
                                                   any(keyword in food_name_lower for keyword in lunch_keywords) or \
                                                   any(keyword in food_type_lower for keyword in lunch_keywords)
                                    
                                    # Check if it's obviously not a lunch food
                                    is_non_lunch = any(keyword in food_name_lower for keyword in non_lunch_keywords)
                                    
                                    if is_lunch_food and not is_non_lunch:
                                        lunch_foods.append(food)
                                    elif not is_non_lunch:
                                        other_foods.append(food)
                                
                                # If we found lunch foods, use them; otherwise use other foods
                                if lunch_foods:
                                    results = lunch_foods[:5]
                                elif other_foods:
                                    results = other_foods[:5]
                                else:
                                    results = all_foods[:5]
                            elif meal_type.lower() == "breakfast":
                                query = "SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods WHERE LOWER(diet_category) = %s AND (LOWER(type) LIKE '%breakfast%' OR LOWER(food_name) LIKE '%cereal%' OR LOWER(food_name) LIKE '%toast%' OR LOWER(food_name) LIKE '%porridge%') ORDER BY food_name LIMIT %s"
                                cursor.execute(query, (diet_category.lower(), 5))
                                results = cursor.fetchall()
                                
                                # If still no results, get any breakfast foods
                                if not results:
                                    query = "SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods WHERE LOWER(type) LIKE '%breakfast%' OR LOWER(food_name) LIKE '%cereal%' OR LOWER(food_name) LIKE '%toast%' OR LOWER(food_name) LIKE '%porridge%' ORDER BY RAND() LIMIT %s"
                                    cursor.execute(query, (5,))
                                    results = cursor.fetchall()
                            elif meal_type.lower() == "dinner":
                                query = "SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods WHERE LOWER(diet_category) = %s AND (LOWER(type) LIKE '%main%' OR LOWER(type) LIKE '%dinner%' OR LOWER(food_name) LIKE '%curry%' OR LOWER(food_name) LIKE '%steak%') ORDER BY food_name LIMIT %s"
                                cursor.execute(query, (diet_category.lower(), 5))
                                results = cursor.fetchall()
                            elif meal_type.lower() == "snack":
                                query = "SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods WHERE LOWER(diet_category) = %s AND (LOWER(type) LIKE '%snack%' OR LOWER(food_name) LIKE '%nuts%' OR LOWER(food_name) LIKE '%fruit%') ORDER BY food_name LIMIT %s"
                                cursor.execute(query, (diet_category.lower(), 5))
                                results = cursor.fetchall()
                            
                            cursor.close()
                            connection.close()
                        except Error as e:
                            print(f"❌ Special meal type query error: {e}")
                
                # If still no results, try with just the dietary restriction
                if not results and diet_category:
                    results = query_foods_by_filters(
                        diet_category=diet_category, 
                        limit=5, 
                        offset=0
                    )
                
                # If still no results, try with just the meal type
                if not results and meal_type:
                    results = query_foods_by_filters(
                        meal_type=meal_type, 
                        limit=5, 
                        offset=0
                    )
                
                # If still no results, get some random foods
                if not results:
                    connection = get_db_connection()
                    if connection:
                        try:
                            cursor = connection.cursor(dictionary=True)
                            cursor.execute("SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods ORDER BY RAND() LIMIT 5")
                            results = cursor.fetchall()
                            cursor.close()
                            connection.close()
                        except Error as e:
                            print(f"❌ Random foods query error: {e}")
                
                if results:
                    reply = "Here are some great options for you:\n\n"
                    for food in results:
                        reply += f"• **{food['food_name']}**: {food['calories']} cal, {food['protein']}g protein ({food['cuisine']}, {food['diet_category']})\n"
                    
                    entities['last_recommendation'] = {
                        'cuisine': cuisine, 
                        'diet_category': diet_category, 
                        'max_calories': max_calories,
                        'min_protein': min_protein,
                        'meal_type': meal_type, 
                        'offset': 5, 
                        'order_by': order_by_clause
                    }
                    context_manager.save_context(intent, entities)
                else:
                    reply = "I couldn't find foods matching those criteria. Try adjusting your filters or asking about specific foods!"
                    context_manager.save_context(intent, entities)

        # CONTEXT FOLLOW-UP (ENHANCED WITH PROPER "MORE" HANDLING)
        elif intent == "context_followup":
            last_context = context_manager.get_last_context()
            message_lower = corrected_message.lower()
            
            # Check if user wants more recommendations
            if any(word in message_lower for word in ["more", "else", "another", "what else", "give me more", "other?", "any more", "anything else"]):
                # Try to get the last recommendation data from context
                last_entities = last_context.get('last_entities', {})
                last_rec_data = last_entities.get('last_recommendation')
                
                # If not found in last_entities, try directly in context
                if not last_rec_data:
                    last_rec_data = last_context.get('last_recommendation')
                
                # If still not found, try to get from the last intent
                if not last_rec_data and last_context.get('last_intent') == 'food_recommendation':
                    # Create a basic recommendation data based on the last entities
                    last_rec_data = {
                        'cuisine': last_entities.get('cuisines', [None])[0],
                        'diet_category': last_entities.get('dietary_restrictions', [None])[0],
                        'max_calories': None,
                        'min_protein': None,
                        'meal_type': last_entities.get('meal_types', [None])[0],
                        'offset': 0,
                        'order_by': None
                    }
                
                if last_rec_data:
                    # First try to get more results with the same criteria
                    results = query_foods_by_filters(
                        cuisine=last_rec_data.get('cuisine'), 
                        diet_category=last_rec_data.get('diet_category'), 
                        max_calories=last_rec_data.get('max_calories'),
                        min_protein=last_rec_data.get('min_protein'),
                        meal_type=last_rec_data.get('meal_type'),
                        limit=5, 
                        offset=last_rec_data.get('offset', 0),
                        order_by=last_rec_data.get('order_by')
                    )
                    
                    # If no more results with the same criteria, try the special meal type logic
                    if not results and last_rec_data.get('diet_category') and last_rec_data.get('meal_type'):
                        connection = get_db_connection()
                        if connection:
                            try:
                                cursor = connection.cursor(dictionary=True)
                                meal_type = last_rec_data.get('meal_type').lower()
                                diet_category = last_rec_data.get('diet_category').lower()
                                
                                if meal_type == "lunch":
                                    # Get all foods with the dietary restriction
                                    query = "SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods WHERE LOWER(diet_category) = %s ORDER BY RAND() LIMIT 20"
                                    cursor.execute(query, (diet_category,))
                                    all_foods = cursor.fetchall()
                                    
                                    # Get manual lunch foods list
                                    manual_lunch_foods = get_manual_lunch_foods()
                                    
                                    # Filter for lunch-appropriate foods
                                    lunch_keywords = [
                                        'curry', 'rice', 'dal', 'sabzi', 'roti', 'wrap', 'sandwich', 'bowl', 'salad', 'meal', 'main', 
                                        'thali', 'biriyani', 'pulao', 'khichdi', 'paratha', 'naan', 'rajma', 'chana', 'masala', 'korma',
                                        'paneer', 'vegetable', 'mix', 'combo', 'platter', 'special', 'dish'
                                    ]
                                    
                                    # Also filter out obvious non-lunch items
                                    non_lunch_keywords = [
                                        'butter', 'milk', 'almond', 'cashew', 'walnut', 'oil', 'ghee', 'sugar', 'honey', 'jam',
                                        'sauce', 'spread', 'drink', 'juice', 'shake', 'smoothie'
                                    ]
                                    
                                    lunch_foods = []
                                    other_foods = []
                                    
                                    for food in all_foods:
                                        food_name_lower = food['food_name'].lower()
                                        food_type_lower = food['type'].lower() if food['type'] else ""
                                        
                                        # Check if it's a known lunch food
                                        is_known_lunch = any(lunch_food.lower() in food_name_lower for lunch_food in manual_lunch_foods)
                                        
                                        # Check if food name or type contains lunch keywords
                                        is_lunch_food = is_known_lunch or \
                                                       any(keyword in food_name_lower for keyword in lunch_keywords) or \
                                                       any(keyword in food_type_lower for keyword in lunch_keywords)
                                        
                                        # Check if it's obviously not a lunch food
                                        is_non_lunch = any(keyword in food_name_lower for keyword in non_lunch_keywords)
                                        
                                        if is_lunch_food and not is_non_lunch:
                                            lunch_foods.append(food)
                                        elif not is_non_lunch:
                                            other_foods.append(food)
                                    
                                    # If we found lunch foods, use them; otherwise use other foods
                                    if lunch_foods:
                                        results = lunch_foods[:5]
                                    elif other_foods:
                                        results = other_foods[:5]
                                    else:
                                        results = all_foods[:5]
                                elif meal_type == "breakfast":
                                    query = "SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods WHERE LOWER(diet_category) = %s AND (LOWER(type) LIKE '%breakfast%' OR LOWER(food_name) LIKE '%cereal%' OR LOWER(food_name) LIKE '%toast%' OR LOWER(food_name) LIKE '%porridge%') ORDER BY food_name LIMIT %s OFFSET %s"
                                    cursor.execute(query, (diet_category, 5, last_rec_data.get('offset', 0)))
                                    results = cursor.fetchall()
                                    
                                    # If still no results, get any breakfast foods
                                    if not results:
                                        query = "SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods WHERE LOWER(type) LIKE '%breakfast%' OR LOWER(food_name) LIKE '%cereal%' OR LOWER(food_name) LIKE '%toast%' OR LOWER(food_name) LIKE '%porridge%' ORDER BY RAND() LIMIT %s OFFSET %s"
                                        cursor.execute(query, (5, last_rec_data.get('offset', 0)))
                                        results = cursor.fetchall()
                                elif meal_type == "dinner":
                                    query = "SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods WHERE LOWER(diet_category) = %s AND (LOWER(type) LIKE '%main%' OR LOWER(type) LIKE '%dinner%' OR LOWER(food_name) LIKE '%curry%' OR LOWER(food_name) LIKE '%steak%') ORDER BY food_name LIMIT %s OFFSET %s"
                                    cursor.execute(query, (diet_category, 5, last_rec_data.get('offset', 0)))
                                    results = cursor.fetchall()
                                elif meal_type == "snack":
                                    query = "SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods WHERE LOWER(diet_category) = %s AND (LOWER(type) LIKE '%snack%' OR LOWER(food_name) LIKE '%nuts%' OR LOWER(food_name) LIKE '%fruit%') ORDER BY food_name LIMIT %s OFFSET %s"
                                    cursor.execute(query, (diet_category, 5, last_rec_data.get('offset', 0)))
                                    results = cursor.fetchall()
                                
                                cursor.close()
                                connection.close()
                            except Error as e:
                                print(f"❌ Special meal type follow-up query error: {e}")
                    
                    # If still no results, try with just the dietary restriction
                    if not results and last_rec_data.get('diet_category'):
                        results = query_foods_by_filters(
                            diet_category=last_rec_data.get('diet_category'), 
                            limit=5, 
                            offset=last_rec_data.get('offset', 0)
                        )
                    
                    # If still no results, try with just the meal type
                    if not results and last_rec_data.get('meal_type'):
                        results = query_foods_by_filters(
                            meal_type=last_rec_data.get('meal_type'), 
                            limit=5, 
                            offset=last_rec_data.get('offset', 0)
                        )
                    
                    # If still no results, get some random foods
                    if not results:
                        connection = get_db_connection()
                        if connection:
                            try:
                                cursor = connection.cursor(dictionary=True)
                                cursor.execute("SELECT food_name, calories, protein, type, cuisine, diet_category FROM foods ORDER BY RAND() LIMIT 5")
                                results = cursor.fetchall()
                                cursor.close()
                                connection.close()
                            except Error as e:
                                print(f"❌ Random foods query error: {e}")
                    
                    if results:
                        reply = "Sure, here are some more options:\n\n"
                        for food in results:
                            reply += f"• **{food['food_name']}**: {food['calories']} cal, {food['protein']}g protein ({food['cuisine']}, {food['diet_category']})\n"
                        
                        updated_rec_data = {**last_rec_data, 'offset': last_rec_data.get('offset', 0) + 5}
                        context_manager.update_context(last_recommendation=updated_rec_data)
                    else:
                        reply = "That's all I could find for those criteria. Would you like to try something else?"
                else:
                    reply = "I'm not sure what you'd like more of. Could you be more specific?"
            
            # Handle simple affirmations as exercise follow-up
            elif any(word in message_lower for word in ["yes", "yeah", "yep", "sure", "ok", "okay", "please", "tell me"]):
                last_entities = last_context.get('last_entities', {})
                last_exercise_topic = last_entities.get('last_exercise_topic')
                
                if last_exercise_topic == 'weight_loss':
                    reply = "🔥 **Specific Weight Loss Exercises:**\n\n"
                    reply += "**Cardio Exercises:**\n"
                    reply += "• Running: 300-400 calories/hour\n"
                    reply += "• Cycling: 250-350 calories/hour\n"
                    reply += "• Swimming: 350-450 calories/hour\n\n"
                    reply += "**Strength Training:**\n"
                    reply += "• Squats: 5-8 calories/minute\n"
                    reply += "• Push-ups: 7-9 calories/minute\n"
                    reply += "• Lunges: 6-8 calories/minute\n\n"
                    reply += "**HIIT Workouts:**\n"
                    reply += "• Burpees: 10-15 calories/minute\n"
                    reply += "• Jumping Jacks: 8-12 calories/minute\n"
                    reply += "• Mountain Climbers: 9-12 calories/minute\n\n"
                    reply += "Would you like a detailed workout plan for any of these?"
                elif last_exercise_topic == 'weight_gain':
                    reply = "💪 **Specific Muscle Building Exercises:**\n\n"
                    reply += "**Compound Movements:**\n"
                    reply += "• Squats: Targets quads, glutes, hamstrings\n"
                    reply += "• Deadlifts: Works entire posterior chain\n"
                    reply += "• Bench Press: Targets chest, shoulders, triceps\n"
                    reply += "• Overhead Press: Focus on shoulders and triceps\n\n"
                    reply += "**Isolation Exercises:**\n"
                    reply += "• Bicep Curls: For arm growth\n"
                    reply += "• Tricep Extensions: For arm definition\n"
                    reply += "• Lateral Raises: For shoulder width\n\n"
                    reply += "**Sample Weekly Split:**\n"
                    reply += "• Monday: Chest & Triceps\n"
                    reply += "• Tuesday: Back & Biceps\n"
                    reply += "• Wednesday: Rest\n"
                    reply += "• Thursday: Legs\n"
                    reply += "• Friday: Shoulders & Abs\n"
                    reply += "• Weekend: Rest or light cardio\n\n"
                    reply += "Would you like more details on any of these exercises?"
                else:
                    reply = "I'd be happy to help! Could you please be more specific about what you'd like to know?"
            
            # Check if user is asking about the last food mentioned
            elif last_context and last_context.get("last_food"):
                food_name = last_context["last_food"]
                is_limiting_request = any(word in message_lower for word in ["only", "just"])
                
                if is_limiting_request:
                    if "calories" in message_lower:
                        food_data = query_food_by_name(food_name)
                        if food_data: 
                            reply = f"• {food_data['food_name']}: {food_data['calories']} kcal"
                        else: 
                            reply = f"Sorry, I don't have information for {food_name}."
                    elif "protein" in message_lower:
                        food_data = query_food_by_name(food_name)
                        if food_data: 
                            reply = f"• {food_data['food_name']}: {food_data['protein']}g protein"
                        else: 
                            reply = f"Sorry, I don't have information for {food_name}."
                    else:
                        reply = f"We were talking about {food_name}. What specifically would you like to know about it?"
                else:
                    reply = f"We were just talking about {food_name}. Did you want to ask something specific about it?"
            else:
                reply = "I'm not sure what you're referring to. Could you please be more specific?"
        
        # FOOD QUERY INTENT
        elif intent == "food_query" and entities["foods"]:
            food_data = None
            food_to_search = None
            
            for food_name in entities["foods"]:
                print(f"🍎 Searching for food: {food_name}")
                food_data = query_food_by_name(food_name)
                if food_data:
                    food_to_search = food_name
                    break
            
            if food_data:
                message_lower = corrected_message.lower()
                if "calories" in message_lower:
                    reply = f"• {food_data['food_name']}: {food_data['calories']} kcal"
                else:
                    reply = f"**{food_data['food_name']}** ({food_data['cuisine']} cuisine, {food_data['diet_category']})\n\n"
                    reply += f"📊 **Nutrition Info:**\n"
                    reply += f"• Calories: {food_data['calories']} kcal\n"
                    reply += f"• Protein: {food_data['protein']}g\n"
                    reply += f"• Carbs: {food_data['carbs']}g\n"
                    reply += f"• Fats: {food_data['fats']}g\n"
                    reply += f"• Type: {food_data['type']}\n\n"
                    if food_data['description']:
                        reply += f"ℹ️ {food_data['description']}\n\n"
                
                if entities["numbers"]:
                    quantity = entities["numbers"][0]
                    adjusted_calories = int(food_data['calories'] * quantity)
                    reply += f"For {quantity} serving(s): approximately **{adjusted_calories} calories**"
                
                context_manager.save_context(intent, {"last_food": food_to_search})
            else:
                reply = f"I couldn't find information for any of these: {', '.join(entities['foods'])}. "
                fallbacks = get_food_fallbacks(entities["foods"][0], limit=3)
                if fallbacks:
                    reply += "Here are some similar options:\n\n"
                    for fb in fallbacks:
                        reply += f"• **{fb['food_name']}**: {fb['calories']} calories ({fb['type']})\n"
                else:
                    reply += "Try asking about common foods like banana, chicken, rice, or paneer!"
        
        # DIET TIP INTENT
        elif intent == "diet_tip":
            message_lower = corrected_message.lower()
            
            # Check if the user is asking about a specific food
            if any(food in message_lower for food in ["rice", "bread", "pasta", "potato", "sugar", "salt", "oil", "butter"]):
                if "rice" in message_lower:
                    reply = "🍚 **Is Rice Healthy?**\n\n"
                    reply += "White rice is a good source of energy but lacks fiber and nutrients. Brown rice is healthier as it contains more fiber, vitamins, and minerals.\n\n"
                    reply += "For a balanced diet, choose brown rice or mix white rice with other whole grains."
                elif "bread" in message_lower:
                    reply = "🍞 **Is Bread Healthy?**\n\n"
                    reply += "Whole grain bread is healthier than white bread as it contains more fiber, vitamins, and minerals.\n\n"
                    reply += "Look for bread with \"100% whole grain\" on the label for the healthiest option."
                elif "sugar" in message_lower:
                    reply = "🍬 **Is Sugar Healthy?**\n\n"
                    reply += "Excessive sugar intake can lead to weight gain, diabetes, and other health issues.\n\n"
                    reply += "Limit added sugars and choose natural sweeteners like fruits instead."
                elif "salt" in message_lower:
                    reply = "🧂 **Is Salt Healthy?**\n\n"
                    reply += "Your body needs some salt, but too much can increase blood pressure and risk of heart disease.\n"
                    reply += "Aim for less than 2,300mg of sodium per day (about 1 teaspoon of salt)."
                elif "oil" in message_lower:
                    reply = "🥑 **Is Oil Healthy?**\n\n"
                    reply += "Healthy fats like olive oil, avocado oil, and coconut oil are good in moderation.\n"
                    reply += "Limit saturated and trans fats found in processed foods."
                elif "butter" in message_lower:
                    reply = "🧈 **Is Butter Healthy?**\n\n"
                    reply += "Butter is high in saturated fat, so use it sparingly.\n"
                    reply += "Consider healthier alternatives like olive oil or avocado oil."
                else:
                    reply = choose_random("diet_tip")
            else:
                reply = choose_random("diet_tip")
            
            context_manager.save_context(intent, entities)
        
        # HYDRATION INTENT
        elif intent == "hydration":
            reply = choose_random("hydration")
            context_manager.save_context(intent, entities)
        
        # BMI/BMR INTENT
        elif intent == "bmi_bmr":
            reply = choose_random("bmi_bmr")
            context_manager.save_context(intent, entities)
        
        # EXERCISE INTENT (FIXED: Replaced DB query with static list to prevent errors)
        elif intent == "exercise":
            message_lower = corrected_message.lower()
            
            # Check if it's a weight loss specific query
            if any(word in message_lower for word in ["weight loss", "lose weight", "burn fat", "reduce weight", "slimming", "fat loss"]):
                reply = "🔥 **Weight Loss Workout Plan:**\n\n"
                reply += "For effective weight loss, focus on a combination of:\n\n"
                reply += "• **Cardio (3-5 days/week):** 30-45 minutes of running, cycling, swimming, or brisk walking\n"
                reply += "• **Strength Training (2-3 days/week):** Full body workouts with compound exercises\n"
                reply += "• **HIIT (1-2 days/week):** High-intensity interval training for maximum calorie burn\n\n"
                reply += "Remember to combine exercise with a balanced diet for best results!\n\n"
                reply += "Would you like specific exercises for any of these categories?"
                
                # Save context for follow-up
                entities['last_exercise_topic'] = 'weight_loss'
                context_manager.save_context(intent, entities)
            
            # Check if it's a weight gain specific query
            elif any(word in message_lower for word in ["weight gain", "gain weight", "build muscle", "muscle building", "bulk up"]):
                reply = "💪 **Weight Gain & Muscle Building Plan:**\n\n"
                reply += "For healthy weight gain and muscle building:\n\n"
                reply += "• **Strength Training (4-5 days/week):** Focus on compound exercises like squats, deadlifts, bench press\n"
                reply += "• **Progressive Overload:** Gradually increase weight or reps each week\n"
                reply += "• **Limited Cardio:** 2-3 light sessions per week (20 minutes)\n"
                reply += "• **Caloric Surplus:** Eat 300-500 calories above your maintenance level\n"
                reply += "• **Protein Intake:** Aim for 1.6-2.2g protein per kg of body weight\n\n"
                reply += "Would you like specific exercises for muscle building?"
                
                # Save context for follow-up
                entities['last_exercise_topic'] = 'weight_gain'
                context_manager.save_context(intent, entities)
            
            else:
                # Instead of relying on database, provide a static list of exercises
                reply = "🏋️ **Exercise Recommendations:**\n\n"
                
                exercises = [
                    {"name": "Running", "calories": "300-400", "difficulty": "Moderate"},
                    {"name": "Cycling", "calories": "250-350", "difficulty": "Moderate"},
                    {"name": "Swimming", "calories": "350-450", "difficulty": "Moderate"},
                    {"name": "Push-ups", "calories": "100-150", "difficulty": "Easy to Moderate"},
                    {"name": "Squats", "calories": "150-200", "difficulty": "Easy to Moderate"},
                    {"name": "Jumping Jacks", "calories": "100-150", "difficulty": "Easy"},
                    {"name": "Burpees", "calories": "200-300", "difficulty": "Hard"},
                    {"name": "Yoga", "calories": "150-200", "difficulty": "Easy to Moderate"},
                    {"name": "Weight Training", "calories": "200-300", "difficulty": "Moderate to Hard"},
                    {"name": "Walking", "calories": "150-200", "difficulty": "Easy"}
                ]
                
                # Select 5 random exercises
                selected_exercises = random.sample(exercises, min(5, len(exercises)))
                
                for ex in selected_exercises:
                    reply += f"• **{ex['name']}**: {ex['calories']} cal/30min ({ex['difficulty']})\n"
                
                reply += "\nWould you like more details about any of these exercises?"
                
                context_manager.save_context(intent, entities)
        
        # FAQ INTENT
        elif intent == "faq":
            reply = "I can help with questions about:\n\n"
            reply += "• Nutrition information for different foods\n"
            reply += "• Food recommendations based on dietary preferences\n"
            reply += "• Diet and nutrition tips\n"
            reply += "• Exercise recommendations\n"
            reply += "• Hydration advice\n"
            reply += "• BMI and BMR information\n\n"
            reply += "What would you like to know more about?"
            context_manager.save_context(intent, entities)
        
        # CHAT HISTORY INTENT
        elif intent == "faq" and ("discuss" in corrected_message.lower() or "talk about" in corrected_message.lower()):
            history = get_user_chat_history(user_id, limit=5)
            if history:
                reply = "Here's what we've talked about recently:\n\n"
                for item in history:
                    # Format the timestamp to be more readable
                    ts = datetime.fromisoformat(item['timestamp']).strftime('%Y-%m-%d %H:%M')
                    reply += f"👤 **You ({ts}):** {item['message']}\n"
                    reply += f"🤖 **Me:** {item['reply'][:80]}{'...' if len(item['reply']) > 80 else ''}\n\n"
                reply += "What would you like to know more about?"
            else:
                reply = "I don't think we've talked about anything yet. How can I help you?"
            
            context_manager.save_context(intent, entities)
        
        # FALLBACK
        else:
            reply = choose_random("fallback")
            context_manager.save_context(intent, entities)
        
        # Save the interaction to the database
        save_chat_to_db(user_id, session_id, user_message, reply, intent)
        
        response = {"intent": intent, "entities": entities, "reply": reply, "timestamp": datetime.now().isoformat()}
        print(f"💬 Reply: {reply[:100]}...")
        print(f"{'='*50}\n")
        
        return jsonify(response), 200
        
    except Exception as e:
        print(f"❌ Error in chat endpoint: {str(e)}")
        import traceback
        traceback.print_exc()
        return jsonify({"error": "Internal server error", "reply": "Sorry, I encountered an error. Please try again.", "details": str(e)}), 500

# Health check endpoint
@app.route('/', methods=['GET'])
def health_check():
    return jsonify({"status": "online", "service": "HealthifyMe Chatbot", "version": "1.0.0", "endpoints": {"chat": "/chat (POST)", "health": "/ (GET)"}})

if __name__ == '__main__':
    print("\n" + "="*60)
    print("🤖 HealthifyMe Chatbot Starting...")
    print("="*60)
    if nlp: 
        print("✅ spaCy NER ready")
    else: 
        print("⚠️  spaCy not loaded - using basic extraction")
    
    test_conn = get_db_connection()
    if test_conn: 
        print("✅ Database connection successful")
        test_conn.close()
    else: 
        print("⚠️  Database connection failed - check credentials")
    
    print("\n🌐 Server starting on http://localhost:5000")
    print("📚 Test endpoint: http://localhost:5000/")
    print("💬 Chat endpoint: http://localhost:5000/chat (POST)")
    print("\nPress Ctrl+C to stop the server\n")
    print("="*60 + "\n")
    app.run(debug=True, host='0.0.0.0', port=5000)