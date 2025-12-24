import os
import pickle

# Simple rule-based intent classifier as fallback
def create_simple_classifier():
    print("Creating a simple rule-based classifier...")
    
    # This is a simple keyword-based classifier
    classifier_data = {
        'food_query': ['calories in', 'how many calories', 'nutrition in', 'protein in', 'carbs in'],
        'food_recommendation': ['suggest', 'recommend', 'give me', 'list of', 'what are some'],
        'greeting': ['hello', 'hi', 'hey', 'good morning'],
        'context_followup': ['more', 'else', 'another', 'what else', 'give me more'],
        'diet_tip': ['diet', 'healthy', 'nutrition advice'],
        'hydration': ['water', 'hydration', 'drink'],
        'bmi_bmr': ['bmi', 'bmr', 'weight'],
        'exercise': ['exercise', 'workout', 'fitness'],
        'faq': ['how', 'what', 'why', 'question'],
        'fallback': ['who are you', 'what is your name', 'tell me a joke']
    }
    
    # Save the classifier data
    with open('intent_classifier_model.pkl', 'wb') as f:
        pickle.dump(classifier_data, f)
    
    print("✅ Simple rule-based classifier created and saved successfully!")

if __name__ == "__main__":
    try:
        create_simple_classifier()
    except Exception as e:
        print(f"Error creating classifier: {e}")
        print("Please check if you have write permissions in this directory.")