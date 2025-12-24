import os

# This will try to open and read the .env file in the current directory
try:
    with open('.env', 'r') as f:
        content = f.read()
        print("✅ SUCCESS: .env file found and is readable.")
        print("--- CONTENT OF .env FILE ---")
        print(content)
        print("--- END OF CONTENT ---")
except FileNotFoundError:
    print("❌ ERROR: The .env file was NOT found in the current directory.")
    print("Please make sure it is in the same folder as check_env.py and chatbot_api.py")
except Exception as e:
    print(f"❌ ERROR: An unexpected error occurred while reading the file: {e}")
