-- HealthifyMe Database Schema
-- MySQL Database for fitness tracking and chatbot
-- Character Set: UTF8MB4 for full Unicode support

DROP DATABASE IF EXISTS fitness_db;
CREATE DATABASE fitness_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fitness_db;

-- ============================================
-- TABLE: users
-- Stores user account information
-- ============================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- In production, use hashed passwords
    age INT,
    height_cm INT,
    weight_kg FLOAT,
    goal VARCHAR(50) DEFAULT 'maintenance', -- Options: weight_loss, muscle_gain, maintenance
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: foods
-- Main food database with 190 items
-- 70 Indian + 70 Global + 50 Common/Fallback
-- ============================================
CREATE TABLE foods (
    id INT AUTO_INCREMENT PRIMARY KEY,
    food_name VARCHAR(150) NOT NULL,
    calories INT NOT NULL,
    type VARCHAR(50) NOT NULL, -- protein, carb, fat, vegetable, fruit, dairy
    cuisine VARCHAR(50) NOT NULL, -- Indian, Global, Common
    diet_category VARCHAR(50) NOT NULL, -- vegan, vegetarian, non-veg
    protein FLOAT DEFAULT 0,
    carbs FLOAT DEFAULT 0,
    fats FLOAT DEFAULT 0,
    description TEXT,
    INDEX idx_food_name (food_name),
    INDEX idx_cuisine (cuisine),
    INDEX idx_diet_category (diet_category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- FOODS: 70 Indian Items
-- ============================================
INSERT INTO foods (food_name, calories, type, cuisine, diet_category, protein, carbs, fats, description) VALUES
('Roti (Whole Wheat)', 71, 'carb', 'Indian', 'vegan', 3.1, 15.0, 0.4, 'Basic Indian flatbread made from whole wheat flour'),
('Chapati', 70, 'carb', 'Indian', 'vegan', 3.0, 15.2, 0.3, 'Thin unleavened flatbread, similar to roti'),
('Paratha (Plain)', 126, 'carb', 'Indian', 'vegetarian', 3.0, 18.0, 5.2, 'Layered flatbread cooked with ghee'),
('Aloo Paratha', 230, 'carb', 'Indian', 'vegetarian', 4.5, 30.0, 9.0, 'Stuffed flatbread with spiced potato filling'),
('Dal Tadka', 104, 'protein', 'Indian', 'vegan', 6.5, 15.0, 2.5, 'Yellow lentils tempered with spices'),
('Rajma (Kidney Beans)', 127, 'protein', 'Indian', 'vegan', 8.7, 22.8, 0.5, 'Red kidney beans curry, high in protein'),
('Chole (Chickpeas)', 164, 'protein', 'Indian', 'vegan', 8.9, 27.4, 2.6, 'Spiced chickpea curry'),
('Paneer (100g)', 265, 'protein', 'Indian', 'vegetarian', 18.3, 1.2, 20.8, 'Indian cottage cheese, high in protein'),
('Palak Paneer', 180, 'protein', 'Indian', 'vegetarian', 11.0, 6.0, 13.0, 'Cottage cheese in spinach gravy'),
('Paneer Tikka', 220, 'protein', 'Indian', 'vegetarian', 14.0, 5.0, 16.0, 'Grilled cottage cheese with spices'),
('Idli (1 piece)', 39, 'carb', 'Indian', 'vegan', 2.0, 8.0, 0.2, 'Steamed rice cake from South India'),
('Dosa (Plain)', 133, 'carb', 'Indian', 'vegan', 4.0, 25.0, 2.0, 'Crispy rice and lentil crepe'),
('Masala Dosa', 210, 'carb', 'Indian', 'vegan', 5.0, 35.0, 5.0, 'Dosa stuffed with spiced potato filling'),
('Sambar (1 cup)', 90, 'protein', 'Indian', 'vegan', 4.0, 16.0, 1.0, 'South Indian lentil and vegetable stew'),
('Upma', 90, 'carb', 'Indian', 'vegan', 3.0, 16.0, 2.0, 'Savory semolina porridge with vegetables'),
('Poha', 180, 'carb', 'Indian', 'vegan', 3.5, 35.0, 3.0, 'Flattened rice breakfast dish'),
('Chicken Curry', 180, 'protein', 'Indian', 'non-veg', 25.0, 5.0, 8.0, 'Indian style chicken in spiced gravy'),
('Butter Chicken', 270, 'protein', 'Indian', 'non-veg', 22.0, 6.0, 18.0, 'Chicken in creamy tomato-based sauce'),
('Chicken Tikka', 150, 'protein', 'Indian', 'non-veg', 27.0, 2.0, 4.0, 'Grilled marinated chicken pieces'),
('Tandoori Chicken', 160, 'protein', 'Indian', 'non-veg', 26.0, 3.0, 5.0, 'Chicken marinated in yogurt and spices, roasted'),
('Fish Curry', 140, 'protein', 'Indian', 'non-veg', 20.0, 4.0, 5.0, 'Fish cooked in spicy curry sauce'),
('Prawn Curry', 120, 'protein', 'Indian', 'non-veg', 24.0, 2.0, 2.0, 'Prawns in coconut-based curry'),
('Egg Curry', 210, 'protein', 'Indian', 'non-veg', 13.0, 5.0, 15.0, 'Boiled eggs in spiced gravy'),
('Biryani (Chicken)', 380, 'carb', 'Indian', 'non-veg', 22.0, 45.0, 12.0, 'Fragrant rice dish with chicken and spices'),
('Veg Biryani', 320, 'carb', 'Indian', 'vegan', 8.0, 52.0, 9.0, 'Aromatic rice with mixed vegetables'),
('Khichdi', 120, 'carb', 'Indian', 'vegan', 5.0, 23.0, 1.0, 'Comfort food - rice and lentils cooked together'),
('Moong Dal', 104, 'protein', 'Indian', 'vegan', 7.0, 19.0, 0.4, 'Split yellow lentils, easy to digest'),
('Masoor Dal', 116, 'protein', 'Indian', 'vegan', 9.0, 20.0, 0.4, 'Red lentils, high in iron'),
('Toor Dal', 343, 'protein', 'Indian', 'vegan', 22.0, 62.0, 1.5, 'Pigeon peas, staple in South India'),
('Aloo Gobi', 150, 'vegetable', 'Indian', 'vegan', 3.0, 20.0, 6.0, 'Potato and cauliflower dry curry'),
('Bhindi Masala', 90, 'vegetable', 'Indian', 'vegan', 2.0, 8.0, 5.0, 'Okra stir-fried with spices'),
('Baingan Bharta', 110, 'vegetable', 'Indian', 'vegan', 2.5, 10.0, 6.0, 'Mashed roasted eggplant with spices'),
('Raita (Cucumber)', 60, 'dairy', 'Indian', 'vegetarian', 3.0, 6.0, 2.5, 'Yogurt-based side dish with cucumber'),
('Lassi (Sweet)', 150, 'dairy', 'Indian', 'vegetarian', 4.0, 25.0, 3.0, 'Yogurt-based drink'),
('Lassi (Salted)', 80, 'dairy', 'Indian', 'vegetarian', 4.0, 8.0, 3.0, 'Savory yogurt drink'),
('Dahi (Yogurt)', 60, 'dairy', 'Indian', 'vegetarian', 3.5, 4.7, 3.3, 'Plain Indian yogurt'),
('Ghee (1 tbsp)', 112, 'fat', 'Indian', 'vegetarian', 0, 0, 12.7, 'Clarified butter used in Indian cooking'),
('Coconut Chutney', 80, 'fat', 'Indian', 'vegan', 1.0, 5.0, 6.0, 'South Indian coconut-based condiment'),
('Mint Chutney', 20, 'vegetable', 'Indian', 'vegan', 0.5, 3.0, 0.5, 'Fresh mint and coriander sauce'),
('Tamarind Chutney', 90, 'carb', 'Indian', 'vegan', 0.3, 22.0, 0.1, 'Sweet and tangy tamarind sauce'),
('Pav Bhaji', 310, 'carb', 'Indian', 'vegetarian', 7.0, 40.0, 13.0, 'Spiced vegetable mash with bread rolls'),
('Vada Pav', 290, 'carb', 'Indian', 'vegetarian', 6.0, 40.0, 11.0, 'Potato fritter sandwich in bread'),
('Samosa (1 piece)', 262, 'carb', 'Indian', 'vegetarian', 3.5, 24.0, 17.0, 'Deep-fried pastry with spiced potato filling'),
('Kachori', 186, 'carb', 'Indian', 'vegetarian', 3.0, 20.0, 10.0, 'Deep-fried puffed bread stuffed with lentils'),
('Pakora (Mix Veg)', 180, 'carb', 'Indian', 'vegan', 4.0, 18.0, 10.0, 'Deep-fried vegetable fritters'),
('Dhokla', 160, 'carb', 'Indian', 'vegan', 5.0, 27.0, 3.0, 'Steamed fermented chickpea flour cake'),
('Thepla', 110, 'carb', 'Indian', 'vegan', 3.0, 18.0, 3.0, 'Gujarati flatbread with fenugreek leaves'),
('Methi Paratha', 140, 'carb', 'Indian', 'vegetarian', 4.0, 20.0, 5.0, 'Flatbread with fenugreek leaves'),
('Paneer Butter Masala', 290, 'protein', 'Indian', 'vegetarian', 15.0, 8.0, 22.0, 'Cottage cheese in rich creamy sauce'),
('Kadai Paneer', 240, 'protein', 'Indian', 'vegetarian', 13.0, 7.0, 18.0, 'Cottage cheese with bell peppers in spicy gravy'),
('Malai Kofta', 320, 'protein', 'Indian', 'vegetarian', 8.0, 22.0, 22.0, 'Deep-fried vegetable balls in creamy curry'),
('Aloo Matar', 130, 'vegetable', 'Indian', 'vegan', 4.0, 20.0, 3.0, 'Potato and peas curry'),
('Chana Masala', 180, 'protein', 'Indian', 'vegan', 9.0, 28.0, 4.0, 'Spicy chickpea curry'),
('Pindi Chole', 190, 'protein', 'Indian', 'vegan', 10.0, 30.0, 4.0, 'North Indian style chickpea curry'),
('Sarson ka Saag', 120, 'vegetable', 'Indian', 'vegan', 5.0, 10.0, 6.0, 'Mustard greens curry from Punjab'),
('Makki ki Roti', 95, 'carb', 'Indian', 'vegan', 3.0, 19.0, 1.0, 'Cornmeal flatbread'),
('Jeera Rice', 210, 'carb', 'Indian', 'vegan', 4.0, 45.0, 2.0, 'Cumin-flavored rice'),
('Pulao (Vegetable)', 250, 'carb', 'Indian', 'vegan', 5.0, 48.0, 4.0, 'Mildly spiced rice with vegetables'),
('Lemon Rice', 200, 'carb', 'Indian', 'vegan', 4.0, 42.0, 3.0, 'Tangy South Indian rice dish'),
('Curd Rice', 150, 'carb', 'Indian', 'vegetarian', 5.0, 28.0, 2.0, 'Rice mixed with yogurt, cooling dish'),
('Pongal', 190, 'carb', 'Indian', 'vegan', 6.0, 35.0, 3.0, 'South Indian rice and lentil porridge'),
('Misal Pav', 330, 'protein', 'Indian', 'vegan', 12.0, 45.0, 10.0, 'Spicy sprouted lentil curry with bread'),
('Kanda Poha', 200, 'carb', 'Indian', 'vegan', 4.0, 38.0, 4.0, 'Flattened rice with onions'),
('Besan Chilla', 120, 'protein', 'Indian', 'vegan', 6.0, 15.0, 4.0, 'Savory chickpea flour pancake'),
('Moong Dal Chilla', 110, 'protein', 'Indian', 'vegan', 8.0, 14.0, 2.0, 'Protein-rich lentil pancake'),
('Rava Idli', 80, 'carb', 'Indian', 'vegan', 2.5, 16.0, 1.0, 'Steamed semolina cakes'),
('Medu Vada', 145, 'protein', 'Indian', 'vegan', 5.0, 18.0, 6.0, 'Crispy lentil donuts'),
('Pesarattu', 150, 'protein', 'Indian', 'vegan', 9.0, 24.0, 2.0, 'Green gram dosa, high protein'),
('Appam', 120, 'carb', 'Indian', 'vegan', 2.0, 25.0, 1.0, 'Fermented rice pancakes from Kerala'),
('Puttu', 130, 'carb', 'Indian', 'vegan', 3.0, 28.0, 0.5, 'Steamed rice cake from Kerala');

-- ============================================
-- FOODS: 70 Global Items
-- ============================================
INSERT INTO foods (food_name, calories, type, cuisine, diet_category, protein, carbs, fats, description) VALUES
('Oats (100g)', 389, 'carb', 'Global', 'vegan', 16.9, 66.3, 6.9, 'Whole grain, high in fiber and protein'),
('Greek Yogurt (100g)', 59, 'dairy', 'Global', 'vegetarian', 10.0, 3.6, 0.4, 'Strained yogurt, high protein'),
('Almond Milk (1 cup)', 30, 'dairy', 'Global', 'vegan', 1.0, 1.0, 2.5, 'Plant-based milk alternative'),
('Soy Milk (1 cup)', 80, 'dairy', 'Global', 'vegan', 7.0, 4.0, 4.0, 'High-protein plant milk'),
('Whole Wheat Bread (1 slice)', 80, 'carb', 'Global', 'vegan', 4.0, 14.0, 1.0, 'Bread made from whole wheat flour'),
('White Bread (1 slice)', 75, 'carb', 'Global', 'vegan', 2.5, 14.0, 1.0, 'Refined white flour bread'),
('Brown Rice (cooked 1 cup)', 216, 'carb', 'Global', 'vegan', 5.0, 45.0, 1.8, 'Whole grain rice, more fiber than white'),
('White Rice (cooked 1 cup)', 205, 'carb', 'Global', 'vegan', 4.2, 45.0, 0.4, 'Polished white rice'),
('Quinoa (cooked 1 cup)', 222, 'carb', 'Global', 'vegan', 8.1, 39.4, 3.6, 'Complete protein grain, gluten-free'),
('Pasta (cooked 1 cup)', 221, 'carb', 'Global', 'vegan', 8.1, 43.0, 1.3, 'Italian wheat-based noodles'),
('Whole Wheat Pasta', 174, 'carb', 'Global', 'vegan', 7.5, 37.0, 0.8, 'High-fiber pasta alternative'),
('Chicken Breast (100g)', 165, 'protein', 'Global', 'non-veg', 31.0, 0, 3.6, 'Lean protein source, boneless'),
('Chicken Thigh', 209, 'protein', 'Global', 'non-veg', 26.0, 0, 10.9, 'Juicier cut with more fat than breast'),
('Turkey Breast', 135, 'protein', 'Global', 'non-veg', 30.0, 0, 0.7, 'Very lean poultry protein'),
('Salmon (100g)', 208, 'protein', 'Global', 'non-veg', 20.0, 0, 13.0, 'Fatty fish, high in omega-3'),
('Tuna (canned)', 116, 'protein', 'Global', 'non-veg', 26.0, 0, 0.8, 'Lean fish, convenient protein'),
('Tilapia', 96, 'protein', 'Global', 'non-veg', 20.0, 0, 1.7, 'Mild white fish, low calorie'),
('Shrimp (100g)', 99, 'protein', 'Global', 'non-veg', 24.0, 0.2, 0.3, 'Low-calorie seafood protein'),
('Egg (1 large)', 72, 'protein', 'Global', 'non-veg', 6.3, 0.4, 4.8, 'Complete protein, versatile'),
('Egg White (1 large)', 17, 'protein', 'Global', 'non-veg', 3.6, 0.2, 0.1, 'Pure protein, zero fat'),
('Tofu (100g)', 76, 'protein', 'Global', 'vegan', 8.0, 1.9, 4.8, 'Soy-based protein, versatile'),
('Tempeh (100g)', 193, 'protein', 'Global', 'vegan', 20.0, 7.6, 11.0, 'Fermented soy, high protein'),
('Edamame (1 cup)', 188, 'protein', 'Global', 'vegan', 18.5, 13.8, 8.1, 'Young soybeans, complete protein'),
('Black Beans (cooked)', 132, 'protein', 'Global', 'vegan', 8.9, 23.7, 0.5, 'High-fiber legume'),
('Kidney Beans (cooked)', 127, 'protein', 'Global', 'vegan', 8.7, 22.8, 0.5, 'Red beans, iron-rich'),
('Lentils (cooked 1 cup)', 230, 'protein', 'Global', 'vegan', 17.9, 39.9, 0.8, 'High protein legume, quick cooking'),
('Chickpeas (cooked)', 164, 'protein', 'Global', 'vegan', 8.9, 27.4, 2.6, 'Versatile legume, good fiber'),
('Peanut Butter (2 tbsp)', 188, 'fat', 'Global', 'vegan', 8.0, 7.0, 16.0, 'High-calorie nut spread'),
('Almond Butter (2 tbsp)', 196, 'fat', 'Global', 'vegan', 6.7, 6.0, 18.0, 'Healthier nut butter option'),
('Almonds (28g)', 164, 'fat', 'Global', 'vegan', 6.0, 6.0, 14.0, 'Heart-healthy nuts'),
('Walnuts (28g)', 185, 'fat', 'Global', 'vegan', 4.3, 3.9, 18.5, 'Omega-3 rich nuts'),
('Cashews (28g)', 157, 'fat', 'Global', 'vegan', 5.2, 8.6, 12.4, 'Creamy nuts, good for sauces'),
('Pistachios (28g)', 159, 'fat', 'Global', 'vegan', 5.7, 7.7, 12.9, 'Lower calorie nut option'),
('Chia Seeds (2 tbsp)', 138, 'fat', 'Global', 'vegan', 4.7, 12.0, 8.7, 'Omega-3 and fiber-rich seeds'),
('Flax Seeds (2 tbsp)', 74, 'fat', 'Global', 'vegan', 2.6, 4.0, 6.0, 'High in omega-3 and lignans'),
('Pumpkin Seeds (28g)', 151, 'fat', 'Global', 'vegan', 7.0, 5.0, 13.0, 'Zinc and magnesium-rich'),
('Sunflower Seeds (28g)', 165, 'fat', 'Global', 'vegan', 5.8, 6.8, 14.0, 'Vitamin E rich seeds'),
('Avocado (100g)', 160, 'fat', 'Global', 'vegan', 2.0, 8.5, 14.7, 'Healthy fats, creamy texture'),
('Olive Oil (1 tbsp)', 119, 'fat', 'Global', 'vegan', 0, 0, 13.5, 'Heart-healthy cooking oil'),
('Coconut Oil (1 tbsp)', 121, 'fat', 'Global', 'vegan', 0, 0, 13.5, 'MCT-rich saturated fat'),
('Banana (medium)', 105, 'fruit', 'Global', 'vegan', 1.3, 27.0, 0.4, 'Quick energy fruit, potassium-rich'),
('Apple (medium)', 95, 'fruit', 'Global', 'vegan', 0.5, 25.0, 0.3, 'High fiber fruit'),
('Orange (medium)', 62, 'fruit', 'Global', 'vegan', 1.2, 15.4, 0.2, 'Vitamin C rich citrus'),
('Strawberries (1 cup)', 49, 'fruit', 'Global', 'vegan', 1.0, 12.0, 0.5, 'Low-calorie berry'),
('Blueberries (1 cup)', 84, 'fruit', 'Global', 'vegan', 1.1, 21.0, 0.5, 'Antioxidant-rich berry'),
('Grapes (1 cup)', 104, 'fruit', 'Global', 'vegan', 1.1, 27.3, 0.2, 'Natural sugar fruit'),
('Watermelon (1 cup)', 46, 'fruit', 'Global', 'vegan', 0.9, 11.5, 0.2, 'Hydrating low-calorie fruit'),
('Mango (1 cup)', 99, 'fruit', 'Global', 'vegan', 1.4, 24.7, 0.6, 'Tropical fruit, vitamin A rich'),
('Pineapple (1 cup)', 82, 'fruit', 'Global', 'vegan', 0.9, 21.6, 0.2, 'Tropical fruit with bromelain'),
('Kiwi (1 medium)', 42, 'fruit', 'Global', 'vegan', 0.8, 10.1, 0.4, 'Vitamin C powerhouse'),
('Broccoli (1 cup)', 31, 'vegetable', 'Global', 'vegan', 2.6, 6.0, 0.3, 'Cruciferous vegetable, nutrient-dense'),
('Spinach (1 cup)', 7, 'vegetable', 'Global', 'vegan', 0.9, 1.1, 0.1, 'Leafy green, iron-rich'),
('Kale (1 cup)', 33, 'vegetable', 'Global', 'vegan', 2.2, 6.0, 0.5, 'Superfood leafy green'),
('Lettuce (1 cup)', 5, 'vegetable', 'Global', 'vegan', 0.5, 1.0, 0.1, 'Low-calorie salad base'),
('Tomato (1 medium)', 22, 'vegetable', 'Global', 'vegan', 1.1, 4.8, 0.2, 'Lycopene-rich fruit-vegetable'),
('Cucumber (1 cup)', 16, 'vegetable', 'Global', 'vegan', 0.7, 3.6, 0.1, 'Hydrating low-calorie vegetable'),
('Bell Pepper (1 medium)', 31, 'vegetable', 'Global', 'vegan', 1.0, 7.2, 0.3, 'Vitamin C rich, crunchy'),
('Carrot (1 medium)', 25, 'vegetable', 'Global', 'vegan', 0.6, 6.0, 0.1, 'Beta-carotene rich root vegetable'),
('Sweet Potato (100g)', 86, 'carb', 'Global', 'vegan', 1.6, 20.1, 0.1, 'Nutrient-dense complex carb'),
('Potato (1 medium)', 163, 'carb', 'Global', 'vegan', 4.3, 37.0, 0.2, 'Versatile starchy vegetable'),
('Cauliflower (1 cup)', 25, 'vegetable', 'Global', 'vegan', 2.0, 5.0, 0.3, 'Low-carb cruciferous vegetable'),
('Zucchini (1 cup)', 20, 'vegetable', 'Global', 'vegan', 1.5, 3.9, 0.4, 'Low-calorie summer squash'),
('Mushrooms (1 cup)', 15, 'vegetable', 'Global', 'vegan', 2.2, 2.3, 0.2, 'Umami-rich fungi, vitamin D'),
('Asparagus (1 cup)', 27, 'vegetable', 'Global', 'vegan', 3.0, 5.2, 0.2, 'Spring vegetable, folate-rich'),
('Green Beans (1 cup)', 31, 'vegetable', 'Global', 'vegan', 1.8, 7.0, 0.2, 'Crunchy legume vegetable'),
('Peas (1 cup)', 117, 'protein', 'Global', 'vegan', 7.9, 21.0, 0.6, 'Sweet legume, good protein'),
('Corn (1 cup)', 132, 'carb', 'Global', 'vegan', 5.0, 29.0, 1.8, 'Starchy vegetable, fiber-rich'),
('Cottage Cheese (100g)', 98, 'dairy', 'Global', 'vegetarian', 11.1, 3.4, 4.3, 'High-protein fresh cheese'),
('Mozzarella (28g)', 78, 'dairy', 'Global', 'vegetarian', 6.3, 0.6, 6.0, 'Italian cheese, melts well');

-- ============================================
-- FOODS: 50 Common/Fallback Items
-- ============================================
INSERT INTO foods (food_name, calories, type, cuisine, diet_category, protein, carbs, fats, description) VALUES
('Apple', 95, 'fruit', 'Common', 'vegan', 0.5, 25.0, 0.3, 'Common fruit, keeps doctor away'),
('Banana', 105, 'fruit', 'Common', 'vegan', 1.3, 27.0, 0.4, 'Potassium-rich yellow fruit'),
('Rice', 205, 'carb', 'Common', 'vegan', 4.2, 45.0, 0.4, 'Staple grain worldwide'),
('Egg', 72, 'protein', 'Common', 'non-veg', 6.3, 0.4, 4.8, 'Whole egg with yolk'),
('Bread', 80, 'carb', 'Common', 'vegan', 4.0, 14.0, 1.0, 'Basic sliced bread'),
('Oats', 389, 'carb', 'Common', 'vegan', 16.9, 66.3, 6.9, 'Rolled oats for porridge'),
('Milk', 42, 'dairy', 'Common', 'vegetarian', 3.4, 5.0, 1.0, 'Cow milk per 100ml'),
('Yogurt', 59, 'dairy', 'Common', 'vegetarian', 10.0, 3.6, 0.4, 'Plain yogurt'),
('Tofu', 76, 'protein', 'Common', 'vegan', 8.0, 1.9, 4.8, 'Soy curd, versatile'),
('Lentils', 230, 'protein', 'Common', 'vegan', 17.9, 39.9, 0.8, 'Dried legumes'),
('Chickpeas', 164, 'protein', 'Common', 'vegan', 8.9, 27.4, 2.6, 'Garbanzo beans'),
('Peanut Butter', 188, 'fat', 'Common', 'vegan', 8.0, 7.0, 16.0, 'Spread made from peanuts'),
('Potato', 163, 'carb', 'Common', 'vegan', 4.3, 37.0, 0.2, 'Starchy tuber'),
('Broccoli', 31, 'vegetable', 'Common', 'vegan', 2.6, 6.0, 0.3, 'Green cruciferous vegetable'),
('Spinach', 7, 'vegetable', 'Common', 'vegan', 0.9, 1.1, 0.1, 'Leafy green vegetable'),
('Carrot', 25, 'vegetable', 'Common', 'vegan', 0.6, 6.0, 0.1, 'Orange root vegetable'),
('Tomato', 22, 'vegetable', 'Common', 'vegan', 1.1, 4.8, 0.2, 'Red salad vegetable'),
('Cucumber', 16, 'vegetable', 'Common', 'vegan', 0.7, 3.6, 0.1, 'Green hydrating vegetable'),
('Chicken', 165, 'protein', 'Common', 'non-veg', 31.0, 0, 3.6, 'Poultry meat, lean'),
('Fish', 140, 'protein', 'Common', 'non-veg', 20.0, 4.0, 5.0, 'Generic fish'),
('Salmon', 208, 'protein', 'Common', 'non-veg', 20.0, 0, 13.0, 'Fatty fish, omega-3'),
('Almonds', 164, 'fat', 'Common', 'vegan', 6.0, 6.0, 14.0, 'Tree nuts, crunchy'),
('Walnuts', 185, 'fat', 'Common', 'vegan', 4.3, 3.9, 18.5, 'Brain-shaped nuts'),
('Olive Oil', 119, 'fat', 'Common', 'vegan', 0, 0, 13.5, 'Mediterranean cooking oil'),
('Avocado', 160, 'fat', 'Common', 'vegan', 2.0, 8.5, 14.7, 'Creamy green fruit'),
('Orange', 62, 'fruit', 'Common', 'vegan', 1.2, 15.4, 0.2, 'Citrus fruit'),
('Strawberry', 49, 'fruit', 'Common', 'vegan', 1.0, 12.0, 0.5, 'Red berry'),
('Blueberry', 84, 'fruit', 'Common', 'vegan', 1.1, 21.0, 0.5, 'Small blue berry'),
('Watermelon', 46, 'fruit', 'Common', 'vegan', 0.9, 11.5, 0.2, 'Large watery fruit'),
('Mango', 99, 'fruit', 'Common', 'vegan', 1.4, 24.7, 0.6, 'Tropical sweet fruit'),
('Grapes', 104, 'fruit', 'Common', 'vegan', 1.1, 27.3, 0.2, 'Small round fruit'),
('Pineapple', 82, 'fruit', 'Common', 'vegan', 0.9, 21.6, 0.2, 'Tropical spiky fruit'),
('Sweet Potato', 86, 'carb', 'Common', 'vegan', 1.6, 20.1, 0.1, 'Orange starchy root'),
('Quinoa', 222, 'carb', 'Common', 'vegan', 8.1, 39.4, 3.6, 'Protein-rich pseudo-grain'),
('Pasta', 221, 'carb', 'Common', 'vegan', 8.1, 43.0, 1.3, 'Italian noodles'),
('Cheese', 98, 'dairy', 'Common', 'vegetarian', 11.1, 3.4, 4.3, 'Generic cheese'),
('Butter', 717, 'fat', 'Common', 'vegetarian', 0.9, 0.1, 81.1, 'Dairy fat spread'),
('Honey', 304, 'carb', 'Common', 'vegetarian', 0.3, 82.4, 0, 'Natural sweetener'),
('Corn', 132, 'carb', 'Common', 'vegan', 5.0, 29.0, 1.8, 'Yellow grain kernels'),
('Peas', 117, 'protein', 'Common', 'vegan', 7.9, 21.0, 0.6, 'Green legume'),
('Mushroom', 15, 'vegetable', 'Common', 'vegan', 2.2, 2.3, 0.2, 'Edible fungi'),
('Onion', 40, 'vegetable', 'Common', 'vegan', 1.1, 9.3, 0.1, 'Aromatic bulb vegetable'),
('Garlic', 149, 'vegetable', 'Common', 'vegan', 6.4, 33.1, 0.5, 'Pungent flavoring bulb'),
('Lettuce', 5, 'vegetable', 'Common', 'vegan', 0.5, 1.0, 0.1, 'Salad greens'),
('Cabbage', 25, 'vegetable', 'Common', 'vegan', 1.3, 5.8, 0.1, 'Leafy vegetable'),
('Cauliflower', 25, 'vegetable', 'Common', 'vegan', 2.0, 5.0, 0.3, 'White cruciferous vegetable'),
('Green Beans', 31, 'vegetable', 'Common', 'vegan', 1.8, 7.0, 0.2, 'String beans'),
('Bell Pepper', 31, 'vegetable', 'Common', 'vegan', 1.0, 7.2, 0.3, 'Colorful sweet pepper'),
('Zucchini', 20, 'vegetable', 'Common', 'vegan', 1.5, 3.9, 0.4, 'Summer squash'),
('Eggplant', 25, 'vegetable', 'Common', 'vegan', 1.0, 5.9, 0.2, 'Purple vegetable');

-- ============================================
-- TABLE: diet_tips
-- Goal-based nutrition tips
-- ============================================
CREATE TABLE diet_tips (
    id INT AUTO_INCREMENT PRIMARY KEY,
    goal VARCHAR(50) NOT NULL,
    tip TEXT NOT NULL,
    INDEX idx_goal (goal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO diet_tips (goal, tip) VALUES
('weight_loss', 'Create a calorie deficit of 500 calories per day to lose about 0.5 kg per week safely.'),
('weight_loss', 'Focus on high-protein foods like chicken, fish, tofu, and legumes to stay full longer.'),
('weight_loss', 'Eat plenty of vegetables - they are low in calories but high in nutrients and fiber.'),
('weight_loss', 'Avoid sugary drinks and opt for water, green tea, or black coffee instead.'),
('weight_loss', 'Practice portion control by using smaller plates and eating slowly to recognize fullness.'),
('weight_loss', 'Include healthy fats like avocado, nuts, and olive oil in moderation for satiety.'),
('weight_loss', 'Meal prep on weekends to avoid impulsive unhealthy food choices during the week.'),
('muscle_gain', 'Consume 1.6-2.2 grams of protein per kg of body weight daily for optimal muscle growth.'),
('muscle_gain', 'Eat in a calorie surplus of 300-500 calories above maintenance to support muscle building.'),
('muscle_gain', 'Include complex carbs like oats, brown rice, and sweet potatoes for workout energy.'),
('muscle_gain', 'Time your protein intake around workouts - have protein before and after training.'),
('muscle_gain', 'Don\'t neglect healthy fats - they support hormone production crucial for muscle growth.'),
('muscle_gain', 'Eat frequently - 5-6 smaller meals throughout the day to maintain anabolic state.'),
('muscle_gain', 'Stay hydrated - drink at least 3-4 liters of water daily for muscle recovery.'),
('maintenance', 'Balance your macros: 40% carbs, 30% protein, 30% fats for general maintenance.'),
('maintenance', 'Listen to your hunger cues and eat mindfully without strict restrictions.'),
('maintenance', 'Include a variety of colorful vegetables and fruits for micronutrient diversity.'),
('maintenance', 'Practice the 80/20 rule - eat healthy 80% of the time and enjoy treats 20%.'),
('maintenance', 'Stay active with regular exercise - aim for 150 minutes of moderate activity weekly.'),
('vegan', 'Combine plant proteins like rice and beans to get all essential amino acids.'),
('vegan', 'Take B12 supplements as it\'s not naturally found in plant foods.'),
('vegan', 'Include iron-rich foods like lentils, spinach with vitamin C sources for absorption.'),
('vegan', 'Eat omega-3 rich foods like chia seeds, flax seeds, and walnuts daily.'),
('vegan', 'Ensure adequate calcium intake from fortified plant milks, tofu, and leafy greens.'),
('vegan', 'Consider vitamin D supplementation, especially if you have limited sun exposure.'),
('general', 'Drink at least 8 glasses of water daily for optimal hydration and metabolism.');

-- ============================================
-- TABLE: exercises
-- Exercise database with calorie burn info
-- ============================================
CREATE TABLE exercises (
    id INT AUTO_INCREMENT PRIMARY KEY,
    exercise_name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    calories_burned_per_30min INT NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    description TEXT,
    INDEX idx_type (type),
    INDEX idx_difficulty (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO exercises (exercise_name, type, calories_burned_per_30min, difficulty, description) VALUES
('Running (8 km/h)', 'cardio', 300, 'moderate', 'Steady-pace jogging, great for cardiovascular health'),
('Running (12 km/h)', 'cardio', 450, 'hard', 'Fast running, high-intensity cardio workout'),
('Cycling (moderate)', 'cardio', 260, 'moderate', 'Outdoor or stationary bike at moderate pace'),
('Cycling (vigorous)', 'cardio', 400, 'hard', 'High-intensity cycling, hill climbs or sprints'),
('Swimming (moderate)', 'cardio', 250, 'moderate', 'Freestyle swimming at comfortable pace'),
('Swimming (vigorous)', 'cardio', 420, 'hard', 'Fast-paced swimming with minimal rest'),
('Walking (brisk)', 'cardio', 150, 'easy', 'Fast-paced walking, low-impact cardio'),
('Jump Rope', 'cardio', 350, 'moderate', 'High-intensity skipping, improves coordination'),
('HIIT Training', 'cardio', 450, 'hard', 'High-Intensity Interval Training, burns fat quickly'),
('Burpees', 'cardio', 400, 'hard', 'Full-body explosive movement, intense workout'),
('Push-ups', 'strength', 100, 'moderate', 'Upper body strength exercise targeting chest and arms'),
('Pull-ups', 'strength', 120, 'hard', 'Back and bicep exercise requiring upper body strength'),
('Squats', 'strength', 90, 'moderate', 'Lower body exercise for legs and glutes'),
('Lunges', 'strength', 100, 'moderate', 'Leg exercise for quads, hamstrings, and glutes'),
('Plank', 'strength', 80, 'moderate', 'Core stabilization exercise, builds endurance'),
('Deadlifts', 'strength', 180, 'hard', 'Full-body compound exercise, primarily lower back and legs'),
('Bench Press', 'strength', 150, 'moderate', 'Upper body pushing exercise for chest, shoulders, triceps'),
('Weight Training', 'strength', 130, 'moderate', 'General resistance training with weights'),
('Yoga (gentle)', 'flexibility', 100, 'easy', 'Low-intensity yoga for flexibility and stress relief'),
('Yoga (power)', 'flexibility', 180, 'moderate', 'Dynamic yoga with strength-building poses'),
('Pilates', 'flexibility', 140, 'moderate', 'Core-focused exercises improving posture and flexibility'),
('Stretching', 'flexibility', 60, 'easy', 'Static and dynamic stretches for muscle recovery'),
('Dancing', 'cardio', 220, 'moderate', 'Fun cardio activity, improves coordination'),
('Boxing', 'cardio', 350, 'hard', 'High-intensity combat sport, full-body workout'),
('Rowing', 'cardio', 280, 'moderate', 'Low-impact cardio working entire body');

-- ============================================
-- TABLE: hydration_tips
-- Water intake guidance
-- ============================================
CREATE TABLE hydration_tips (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tip TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO hydration_tips (tip) VALUES
('Drink at least 8-10 glasses (2-3 liters) of water daily for optimal hydration.'),
('Start your day with a glass of warm water to kickstart your metabolism.'),
('Carry a reusable water bottle to remind yourself to drink throughout the day.'),
('Drink water before, during, and after exercise to prevent dehydration.'),
('If you exercise intensely, drink 0.5-1 liter of water per hour of workout.'),
('Monitor your urine color - pale yellow indicates good hydration, dark yellow means drink more.'),
('Eat water-rich foods like cucumber, watermelon, and oranges to boost hydration.'),
('Avoid excessive caffeine and alcohol as they can dehydrate your body.');

-- ============================================
-- TABLE: faqs
-- Frequently asked questions
-- ============================================
CREATE TABLE faqs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question TEXT NOT NULL,
    answer TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO faqs (question, answer) VALUES
('What is BMI?', 'BMI (Body Mass Index) is calculated as weight(kg) / height(m)². BMI 18.5-24.9 is normal, below 18.5 is underweight, 25-29.9 is overweight, and 30+ is obese.'),
('What is BMR?', 'BMR (Basal Metabolic Rate) is the number of calories your body burns at rest. It represents the minimum energy needed for basic body functions like breathing and circulation.'),
('How do I calculate my daily calorie needs?', 'Multiply your BMR by activity factor: Sedentary (1.2), Light activity (1.375), Moderate (1.55), Very active (1.725), Extremely active (1.9).'),
('How much protein should I eat daily?', 'General guideline: 0.8g per kg body weight for sedentary, 1.2-1.6g for active individuals, and 1.6-2.2g for muscle building.'),
('What are macros?', 'Macros (macronutrients) are protein, carbohydrates, and fats - the three main nutrients your body needs in large amounts for energy and growth.'),
('How many calories should I eat to lose weight?', 'Create a deficit of 500 calories from your maintenance level to lose about 0.5 kg per week safely and sustainably.'),
('How many calories should I eat to gain muscle?', 'Eat 300-500 calories above your maintenance level combined with strength training for optimal muscle growth.'),
('What is the best time to eat carbs?', 'Eat carbs around your workout - before for energy and after for recovery. Complex carbs earlier in the day work well for most people.'),
('Should I eat before or after workout?', 'Both! Have a light meal with carbs and protein 1-2 hours before workout, and protein-rich meal within 2 hours after for recovery.'),
('How much water should I drink daily?', 'General recommendation is 8-10 glasses (2-3 liters) daily. Drink more if exercising, in hot weather, or if you have high body weight.'),
('Are carbs bad for weight loss?', 'No! Carbs are essential for energy. Choose complex carbs like oats, brown rice, and whole grains. It\'s about quantity and quality, not elimination.'),
('Can I eat rice and lose weight?', 'Yes! Portion control is key. Opt for brown rice or have smaller portions of white rice with plenty of vegetables and protein.'),
('Is paneer good for weight loss?', 'Paneer is high in protein but also high in calories and fat. Eat in moderation - about 100g provides good protein but has 265 calories.'),
('How can I increase protein on a vegetarian diet?', 'Eat paneer, tofu, legumes (dal, rajma, chole), Greek yogurt, eggs (if you eat them), quinoa, and nuts. Combine plant proteins for complete amino acids.'),
('What should I eat for breakfast to lose weight?', 'High-protein options like egg whites, Greek yogurt, oats with nuts, moong dal chilla, or upma with vegetables keep you full longer.'),
('Is it okay to skip meals for weight loss?', 'Not recommended. Skipping meals can slow metabolism and lead to overeating later. Instead, eat smaller, frequent meals with proper portions.'),
('What are good post-workout snacks?', 'Banana with peanut butter, Greek yogurt, protein shake, boiled eggs, or a small meal with lean protein and carbs within 2 hours of exercise.'),
('How do I reduce belly fat?', 'You cannot spot-reduce fat. Create a calorie deficit through diet and exercise, focus on strength training, and reduce refined carbs and sugar.'),
('Are cheat meals okay?', 'Yes! Having one cheat meal per week can help mentally and prevent feeling deprived. Just don\'t let it turn into a cheat day or derail your progress.'),
('What is clean eating?', 'Eating whole, minimally processed foods like vegetables, fruits, whole grains, lean proteins, and healthy fats while limiting added sugars and artificial ingredients.'),
('How long does it take to see results?', 'Visible changes typically take 4-8 weeks with consistent diet and exercise. Initial weight loss is often water weight. Be patient and consistent.'),
('Should I take supplements?', 'Whole foods should be your primary source. Consider supplements if you have deficiencies: Vitamin D, B12 (for vegans), Omega-3, or protein powder for convenience.'),
('What is intermittent fasting?', 'An eating pattern where you cycle between periods of eating and fasting. Common method is 16:8 (fast 16 hours, eat within 8-hour window).'),
('How many eggs can I eat per day?', 'Healthy individuals can eat 1-2 whole eggs daily. If eating more, use egg whites to reduce cholesterol and calorie intake while getting protein.'),
('Is fruit sugar bad?', 'Natural fruit sugar comes with fiber, vitamins, and antioxidants. It\'s different from added sugar. 2-3 servings of whole fruits daily is healthy.'),
('What causes weight loss plateau?', 'Your body adapts to lower calories. To overcome: adjust calorie intake, increase workout intensity, ensure adequate sleep, and manage stress.'),
('Should I eat before cardio?', 'For moderate cardio, a light snack 30-60 minutes before is fine. For intense workouts, eat a proper meal 2-3 hours before. Stay hydrated.'),
('How important is sleep for fitness?', 'Extremely important! 7-9 hours of quality sleep aids muscle recovery, hormone regulation, metabolism, and appetite control. Poor sleep hinders progress.'),
('Can I drink alcohol and still lose weight?', 'Alcohol has empty calories (7 cal/gram). Occasional moderate drinking is okay but it can slow fat loss. Limit intake and account for calories.'),
('What is the best diet plan?', 'There is no one-size-fits-all. The best diet is one you can sustain long-term, meets your nutritional needs, fits your lifestyle, and creates appropriate calorie balance.');

-- ============================================
-- TABLE: meals
-- User meal logging
-- ============================================
CREATE TABLE meals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    food_id INT,
    food_name VARCHAR(150) NOT NULL,
    calories INT NOT NULL,
    date DATE NOT NULL,
    meal_type VARCHAR(20) NOT NULL, -- breakfast, lunch, dinner, snack
    quantity FLOAT DEFAULT 1.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE SET NULL,
    INDEX idx_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: progress
-- User weight tracking
-- ============================================
CREATE TABLE progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    date DATE NOT NULL,
    weight FLOAT NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: water_logs
-- Hydration tracking
-- ============================================
CREATE TABLE water_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    date DATE NOT NULL,
    liters FLOAT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- SAMPLE DATA for testing
-- ============================================
INSERT INTO users (username, email, password, age, height_cm, weight_kg, goal) VALUES
('demo_user', 'demo@healthifyme.com', 'demo123', 25, 170, 70.0, 'weight_loss'),
('test_user', 'test@healthifyme.com', 'test123', 30, 175, 80.0, 'muscle_gain');

-- ============================================
-- VERIFICATION QUERIES
-- Run these after import to verify data
-- ============================================
-- SELECT COUNT(*) as total_foods FROM foods;
-- SELECT cuisine, COUNT(*) as count FROM foods GROUP BY cuisine;
-- SELECT COUNT(*) as total_tips FROM diet_tips;
-- SELECT COUNT(*) as total_exercises FROM exercises;
-- SELECT COUNT(*) as total_faqs FROM faqs;

-- ============================================
-- END OF DATABASE SCHEMA
-- Total Foods: 190 (70 Indian + 70 Global + 50 Common)
-- Ready for HealthifyMe Application
-- ============================================

-- ============================================
-- FOODS: