import pickle
import os

def get_intent(message):
    # Try to load the classifier model from the current directory
    model_path = os.path.join(os.path.dirname(__file__), '..', 'intent_classifier_model.pkl')
    
    try:
        with open(model_path, 'rb') as f:
            model = pickle.load(f)
            
        # Check if it's a rule-based classifier (dictionary) or ML model
        if isinstance(model, dict):
            # Use rule-based classification with more comprehensive patterns
            message = message.lower()
            
            # Priority-based intent detection
            if any(word in message for word in ["calories in", "how many calories", "nutrition in", "protein in", "carbs in", "fats in"]):
                return "food_query"
            elif any(word in message for word in ["suggest", "recommend", "give me", "list of", "what are some", "show me", "high protein", "low calories"]):
                return "food_recommendation"
            # NEW: Handle simple food requests like "vegan lunch"
            elif any(word in message for word in ["vegan", "vegetarian", "non-veg"]) and any(word in message for word in ["breakfast", "lunch", "dinner", "snack"]):
                return "food_recommendation"
            elif any(word in message for word in ["more", "else", "another", "what else", "give me more", "other?", "any more", "anything else"]):
                return "context_followup"
            # NEW: Handle simple affirmations as context follow-up
            elif any(word in message for word in ["yes", "yeah", "yep", "sure", "ok", "okay", "please", "tell me"]):
                return "context_followup"
            elif any(word in message for word in ["hello", "hi", "hey", "good morning", "good evening"]):
                return "greeting"
            elif any(word in message for word in ["diet", "healthy", "nutrition advice", "eating healthy"]):
                return "diet_tip"
            elif any(word in message for word in ["water", "hydration", "drink", "how much water"]):
                return "hydration"
            # NEW: Better BMI/BMR detection - must be explicit
            elif any(word in message for word in ["bmi", "bmr", "body mass index", "basal metabolic rate"]) or \
                 ("calculate" in message and any(word in message for word in ["bmi", "bmr"])):
                return "bmi_bmr"
            # NEW: Better exercise detection
            elif any(word in message for word in ["exercise", "workout", "fitness", "training", "gym", "cardio", "strength", "yoga", "running", "walking", "cycling", "swimming"]):
                return "exercise"
            # NEW: Better weight loss detection
            elif any(word in message for word in ["weight loss", "lose weight", "burn fat", "reduce weight", "slimming", "fat loss"]):
                return "exercise"  # Map weight loss to exercise intent
            # NEW: Better weight gain detection
            elif any(word in message for word in ["weight gain", "gain weight", "build muscle", "muscle building", "bulk up"]):
                return "exercise"  # Map weight gain to exercise intent
            # NEW: Handle food details queries
            elif any(word in message for word in ["details of", "information about", "tell me about", "what is", "describe"]):
                return "food_query"
            elif any(word in message for word in ["how", "what", "why", "question", "help"]):
                return "faq"
            else:
                return "fallback"
        else:
            # Use ML model
            prediction = model.predict([message])[0]
            return prediction
            
    except Exception as e:
        print(f"Error loading model: {e}")
        # Ultimate fallback to basic rules
        message = message.lower()
        if "calories in" in message or "how many calories" in message:
            return "food_query"
        elif "suggest" in message or "recommend" in message:
            return "food_recommendation"
        # NEW: Handle simple food requests like "vegan lunch"
        elif any(word in message for word in ["vegan", "vegetarian", "non-veg"]) and any(word in message for word in ["breakfast", "lunch", "dinner", "snack"]):
            return "food_recommendation"
        elif "more" in message or "else" in message:
            return "context_followup"
        # NEW: Handle simple affirmations as context follow-up
        elif any(word in message for word in ["yes", "yeah", "yep", "sure", "ok", "okay", "please", "tell me"]):
            return "context_followup"
        # NEW: Better BMI/BMR detection
        elif any(word in message for word in ["bmi", "bmr", "body mass index", "basal metabolic rate"]) or \
             ("calculate" in message and any(word in message for word in ["bmi", "bmr"])):
            return "bmi_bmr"
        # NEW: Better exercise detection
        elif any(word in message for word in ["exercise", "workout", "fitness", "training", "gym", "cardio", "strength", "yoga", "running", "walking", "cycling", "swimming"]):
            return "exercise"
        # NEW: Better weight loss detection
        elif any(word in message for word in ["weight loss", "lose weight", "burn fat", "reduce weight", "slimming", "fat loss"]):
            return "exercise"  # Map weight loss to exercise intent
        # NEW: Better weight gain detection
        elif any(word in message for word in ["weight gain", "gain weight", "build muscle", "muscle building", "bulk up"]):
            return "exercise"  # Map weight gain to exercise intent
        # NEW: Handle food details queries
        elif any(word in message for word in ["details of", "information about", "tell me about", "what is", "describe"]):
            return "food_query"
        else:
            return "fallback"