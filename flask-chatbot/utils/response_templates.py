# ============================================
# Response Templates Utility
# FILE: flask-chatbot/utils/response_templates.py
# ============================================
"""
Response Template Manager

PURPOSE:
Provides varied, natural-sounding responses for different intents.
Uses randomization to avoid repetitive bot replies.

FEATURES:
1. Multiple response variations for each intent
2. Random selection for natural conversation
3. Easy to expand with more templates
4. Supports placeholder formatting

Compatible with:
- Python 3.13.9
- rapidfuzz 3.14.1
- fuzzywuzzy 0.18.0
"""

import random

# ============================================
# RESPONSE TEMPLATES DICTIONARY
# ============================================
# Each intent has multiple response variations
# Random selection makes conversation feel more natural

RESPONSE_TEMPLATES = {
    # Greeting responses
    "greeting": [
        "Hello! 👋 I'm your HealthifyMe fitness assistant. How can I help you today?",
        "Hi there! 😊 Ready to talk about nutrition and fitness?",
        "Hey! Welcome to HealthifyMe! Ask me about calories, foods, diet tips, or exercises.",
        "Namaste! 🙏 I'm here to help with your fitness journey. What would you like to know?",
        "Hello! I can help you with calorie info, food recommendations, diet plans, and more!",
        "Hi! 🌟 Your personal nutrition assistant is here. What's on your mind?",
    ],
    
    # Goodbye responses
    "goodbye": [
        "Goodbye! Stay healthy and keep up the good work! 💪",
        "Take care! Remember to stay hydrated and eat well! 👋",
        "See you later! Keep crushing those fitness goals! 🎯",
        "Bye! Don't forget to drink water and stay active! 💧",
        "Farewell! Wishing you a healthy day ahead! 🌞",
        "Catch you later! Stay fit and fabulous! ✨",
    ],
    
    # Fallback when intent is unclear
    "fallback": [
        "I'm not quite sure what you're asking. Could you rephrase that? 🤔",
        "Hmm, I didn't understand that. Try asking about specific foods, calories, diet tips, or exercises.",
        "I'm still learning! Could you ask in a different way? You can ask about:\n• Calories in food\n• Food recommendations\n• Diet tips\n• Exercise info",
        "Sorry, I didn't catch that. Try questions like:\n• 'How many calories in banana?'\n• 'Suggest vegan foods'\n• 'What is BMI?'",
        "I'm here to help with nutrition and fitness! Ask me about foods, calories, diet plans, or workouts.",
        "Let me help you better! Ask about:\n✓ Specific foods and their nutrition\n✓ Diet recommendations\n✓ Exercise suggestions\n✓ Fitness calculations (BMI, BMR)",
    ],
    
    # When asking user for food name
    "ask_food_name": [
        "Which food would you like to know about? 🍎",
        "Sure! What food are you curious about?",
        "Tell me the food name and I'll find the nutrition info for you!",
        "What food should I look up?",
    ],
    
    # Food suggestion templates
    "food_suggestion": [
        "Here are some great options for you:",
        "I found these foods that match your criteria:",
        "Check out these nutritious options:",
        "Based on your request, here are some foods:",
    ],
    
    # Hydration responses
    "hydration": [
        "💧 Stay hydrated! Aim for at least 8-10 glasses (2-3 liters) of water daily.",
        "Water is essential! Drink 2-3 liters per day, more if you exercise or it's hot outside.",
        "💦 Hydration tip: Drink a glass of water when you wake up, before meals, and during workouts.",
        "Your body needs water! General rule: 30-35ml per kg of body weight daily. Drink more during exercise!",
        "Hydration is key to fitness! Carry a water bottle and sip throughout the day. Aim for pale yellow urine color.",
    ],
    
    # BMI/BMR responses
    "bmi_bmr": [
        "📊 **BMI (Body Mass Index)** = Weight(kg) / Height(m)²\n• 18.5-24.9: Normal\n• 25-29.9: Overweight\n• 30+: Obese\n\n**BMR (Basal Metabolic Rate)** = Calories your body burns at rest.\n• For men: 66 + (13.7 × weight kg) + (5 × height cm) - (6.8 × age)\n• For women: 655 + (9.6 × weight kg) + (1.8 × height cm) - (4.7 × age)",
        
        "🧮 **BMI Calculator:**\nBMI = Your weight in kg ÷ (Your height in meters)²\n\nExample: 70kg, 1.75m → BMI = 70/(1.75×1.75) = 22.9 (Normal)\n\n**BMR Calculator:**\nYour BMR is the calories needed for basic body functions. Multiply BMR by activity factor:\n• Sedentary: BMR × 1.2\n• Light activity: BMR × 1.375\n• Moderate: BMR × 1.55\n• Very active: BMR × 1.725",
        
        "📈 Want to calculate your BMI? Use: Weight(kg) / Height(m)²\n\nFor BMR (daily calorie needs), use Mifflin-St Jeor equation and multiply by your activity level.\n\nI can help you understand these calculations better!",
    ],
    
    # Diet tip responses
    "diet_tip": [
        "🥗 **Diet Tip:** Focus on whole foods - vegetables, fruits, lean proteins, and whole grains. Avoid processed foods and excess sugar.",
        
        "💡 **Healthy Eating:** Eat protein with every meal to stay full longer and maintain muscle mass.",
        
        "🍽️ **Portion Control:** Use smaller plates, eat slowly, and stop when 80% full. Listen to your body!",
        
        "🥑 **Balanced Diet:** Include all macros - carbs for energy, protein for muscle, healthy fats for hormones and satiety.",
        
        "🕐 **Meal Timing:** Don't skip breakfast. Eat every 3-4 hours to maintain energy and avoid overeating later.",
    ],
    
    # No results found
    "no_results": [
        "I couldn't find any foods matching those criteria. Try adjusting your filters!",
        "No matches found. Try broadening your search or ask about specific foods.",
        "Hmm, nothing came up. Try asking about specific foods like 'paneer', 'chicken', or 'oats'.",
    ],
    
    # Database error
    "db_error": [
        "Sorry, I'm having trouble connecting to my database right now. Please try again in a moment.",
        "Oops! Database connection issue. Please check that MySQL is running and try again.",
        "I can't access my food database at the moment. Make sure the database is properly set up.",
    ],
    
    # Exercise responses
    "exercise_general": [
        "💪 Regular exercise is crucial for fitness! Aim for 150 minutes of moderate activity weekly.",
        "🏃 Mix cardio (for heart health) with strength training (for muscle) for best results.",
        "🧘 Don't forget rest days! Your muscles grow during recovery, not during the workout.",
    ],
    
    # Success messages
    "success": [
        "Great! Anything else I can help with?",
        "Hope that helps! Feel free to ask more questions.",
        "Got it! What else would you like to know?",
    ],
}

# ============================================
# HELPER FUNCTION: CHOOSE RANDOM RESPONSE
# ============================================
def choose_random(template_key):
    """
    Select a random response from a template category.
    
    Args:
        template_key (str): Key from RESPONSE_TEMPLATES dictionary
    
    Returns:
        str: Randomly selected response
    
    Example:
        reply = choose_random("greeting")
        # Returns one of the greeting messages randomly
    
    Why randomize?
        Makes the bot feel more natural and less robotic.
        Users won't see the same reply every time.
    """
    # Check if template key exists
    if template_key in RESPONSE_TEMPLATES:
        # Get list of templates for this key
        templates = RESPONSE_TEMPLATES[template_key]
        
        # Return random choice from list
        return random.choice(templates)
    else:
        # If key not found, return generic fallback
        print(f"⚠️  Template key '{template_key}' not found")
        return "I'm here to help with nutrition and fitness! How can I assist you?"

# ============================================
# ADVANCED: FORMAT RESPONSE WITH DATA
# ============================================
def format_response(template_key, **kwargs):
    """
    Choose a random template and format it with provided data.
    
    Args:
        template_key (str): Template category
        **kwargs: Key-value pairs for formatting
    
    Returns:
        str: Formatted response
    
    Example:
        reply = format_response("food_info", 
                               food_name="Banana", 
                               calories=105)
        # If template is "**{food_name}** has {calories} calories"
        # Returns: "**Banana** has 105 calories"
    
    Use case:
        When you need dynamic content in templates
    """
    # Get base template
    template = choose_random(template_key)
    
    # Format with provided variables
    try:
        return template.format(**kwargs)
    except KeyError as e:
        # If formatting fails, return unformatted template
        print(f"⚠️  Missing variable in template: {e}")
        return template

# ============================================
# ADD CUSTOM TEMPLATES
# ============================================
def add_template(template_key, response_text):
    """
    Add a new response template at runtime.
    
    Args:
        template_key (str): Category key
        response_text (str): Response text to add
    
    Example:
        add_template("custom_greeting", "Hello from custom template!")
    
    Use case:
        Dynamically expand bot responses without editing this file
    """
    if template_key in RESPONSE_TEMPLATES:
        # Add to existing category
        RESPONSE_TEMPLATES[template_key].append(response_text)
    else:
        # Create new category
        RESPONSE_TEMPLATES[template_key] = [response_text]
    
    print(f"✅ Added template to '{template_key}'")

# ============================================
# GET ALL TEMPLATES FOR A KEY
# ============================================
def get_all_templates(template_key):
    """
    Get all response variations for a template key.
    
    Args:
        template_key (str): Template category
    
    Returns:
        list: All responses for this category
    
    Use case:
        Review or edit all variations for a specific intent
    """
    return RESPONSE_TEMPLATES.get(template_key, [])

# ============================================
# LIST ALL AVAILABLE TEMPLATE KEYS
# ============================================
def list_template_keys():
    """
    Get list of all available template categories.
    
    Returns:
        list: All template keys
    
    Use case:
        See what response categories exist
    """
    return list(RESPONSE_TEMPLATES.keys())

# ============================================
# TESTING (run this file directly)
# ============================================
if __name__ == "__main__":
    print("Testing Response Templates:")
    print("="*60)
    
    # Test random selection
    print("\n1. Testing Random Responses:")
    for _ in range(3):
        print(f"Greeting: {choose_random('greeting')}")
    
    print("\n2. Testing All Template Keys:")
    print(f"Available templates: {list_template_keys()}")
    
    print("\n3. Testing Fallback:")
    print(choose_random("nonexistent_key"))
    
    print("\n4. Testing Custom Template:")
    add_template("test", "This is a test response!")
    print(choose_random("test"))
    
    print("\n" + "="*60)