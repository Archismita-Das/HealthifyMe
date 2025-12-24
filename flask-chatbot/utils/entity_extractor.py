import re

def extract_entities(message):
    entities = {'foods': [], 'numbers': [], 'dietary_restrictions': [], 'meal_types': [], 'nutrients': [], 'qualifiers': []}
    
    message = message.lower()
    
    # Extract food items
    food_patterns = [
        r'calories in (\w+)',
        r'nutrition in (\w+)',
        r'protein in (\w+)',
        r'(\w+) calories',
        r'(\w+) protein',
        r'suggest (\w+) food',
        r'recommend (\w+) food',
        r'(\w+) breakfast',
        r'(\w+) lunch',
        r'(\w+) dinner',
        r'(\w+) snack'
    ]
    
    for pattern in food_patterns:
        matches = re.findall(pattern, message)
        for match in matches:
            if match not in entities['foods']:
                entities['foods'].append(match)
    
    # Extract dietary restrictions
    restrictions = ['vegan', 'vegetarian', 'gluten-free', 'dairy-free', 'low-carb', 'keto', 'paleo']
    for restriction in restrictions:
        if restriction in message:
            entities['dietary_restrictions'].append(restriction)
    
    # Extract meal types
    meal_types = ['breakfast', 'lunch', 'dinner', 'snack', 'brunch']
    for meal_type in meal_types:
        if meal_type in message:
            entities['meal_types'].append(meal_type)
    
    # Extract nutrients
    nutrients = ['calories', 'protein', 'carbs', 'carbohydrates', 'fats', 'fiber', 'sugar', 'sodium']
    for nutrient in nutrients:
        if nutrient in message:
            entities['nutrients'].append(nutrient)
    
    # Extract qualifiers
    qualifiers = ['high', 'low', 'rich in', 'free of', 'healthy', 'unhealthy']
    for qualifier in qualifiers:
        if qualifier in message:
            entities['qualifiers'].append(qualifier)
    
    # Extract numbers
    numbers = re.findall(r'\d+', message)
    entities['numbers'] = [int(num) for num in numbers]
    
    return entities