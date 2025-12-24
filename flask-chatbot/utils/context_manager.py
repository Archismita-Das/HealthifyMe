# ============================================
# Context Manager Utility
# FILE: flask-chatbot/utils/context_manager.py
# ============================================
"""
Conversation Context Management

PURPOSE:
Remembers previous user queries and context within a conversation session.
Enables the chatbot to answer follow-up questions like "What about that?" or "Tell me more".

FEATURES:
1. Store last intent detected
2. Store last food/entity mentioned
3. Store conversation history
4. Enable context-aware responses

NOTE:
This is a simple in-memory context manager for demonstration.
In production, use Redis or database sessions for multi-user support.
"""

from datetime import datetime

# ============================================
# CHAT CONTEXT CLASS
# ============================================
class ChatContext:
    """
    Manages conversation context for a chat session.
    
    Attributes:
        last_intent (str): The most recent intent detected
        last_food (str): The most recent food mentioned
        history (list): List of recent interactions
        max_history (int): Maximum number of interactions to remember
    
    Example Usage:
        context = ChatContext()
        context.save_context("food_query", {"last_food": "banana"})
        last = context.get_last_context()  # Returns {"last_food": "banana"}
    """
    
    def __init__(self, max_history=10):
        """
        Initialize a new chat context.
        
        Args:
            max_history (int): Maximum conversation history to keep (default: 10)
        
        What happens:
            Creates empty context storage for a new conversation session.
        """
        self.last_intent = None  # Store the most recent intent
        self.last_food = None  # Store the most recent food mentioned
        self.last_entities = {}  # Store all entities from last query
        self.history = []  # List of recent interactions
        self.max_history = max_history  # Limit history size
        self.session_start = datetime.now()  # Track when session started
        
        print("🔄 New chat context initialized")
    
    def save_context(self, intent, entities=None):
        """
        Save current interaction context.
        
        Args:
            intent (str): The detected intent from current query
            entities (dict): Extracted entities (foods, numbers, etc.)
        
        What happens:
            1. Updates last_intent
            2. If entities contain food, updates last_food
            3. Stores entities for future reference
            4. Adds to conversation history
        
        Example:
            context.save_context("food_query", {"last_food": "paneer"})
        """
        # Update last intent
        self.last_intent = intent
        
        # Update entities if provided
        if entities:
            self.last_entities = entities
            
            # Extract and save last food if present
            if "last_food" in entities:
                self.last_food = entities["last_food"]
                print(f"📝 Context saved: last_food = {self.last_food}")
            
            # Alternative: Extract from foods list
            if "foods" in entities and entities["foods"]:
                self.last_food = entities["foods"][0]  # Take first food
                print(f"📝 Context saved: last_food = {self.last_food}")
        
        # Add to history
        self.add_to_history(intent, entities)
    
    def add_to_history(self, intent, entities=None):
        """
        Add current interaction to conversation history.
        
        Args:
            intent (str): Current intent
            entities (dict): Current entities
        
        What happens:
            Appends interaction to history list with timestamp.
            Removes oldest items if history exceeds max_history.
        """
        # Create history entry
        entry = {
            "timestamp": datetime.now().isoformat(),
            "intent": intent,
            "entities": entities
        }
        
        # Add to history
        self.history.append(entry)
        
        # Trim history if too long
        if len(self.history) > self.max_history:
            self.history = self.history[-self.max_history:]  # Keep last N items
    
    def get_last_context(self):
        """
        Retrieve the most recent context.
        
        Returns:
            dict: Dictionary with last_intent, last_food, and last_entities
        
        Example:
            context = ctx.get_last_context()
            if context["last_food"]:
                print(f"Previously discussed: {context['last_food']}")
        """
        return {
            "last_intent": self.last_intent,
            "last_food": self.last_food,
            "last_entities": self.last_entities
        }
    
    def get_history(self, last_n=5):
        """
        Get recent conversation history.
        
        Args:
            last_n (int): Number of recent interactions to return
        
        Returns:
            list: List of recent interactions
        
        Use case:
            Analyze conversation flow or provide context summary.
        """
        return self.history[-last_n:] if self.history else []
    
    def clear_context(self):
        """
        Clear all context and history.
        
        Use case:
            When starting a new conversation or on user request.
        """
        self.last_intent = None
        self.last_food = None
        self.last_entities = {}
        self.history = []
        self.session_start = datetime.now()
        print("🔄 Context cleared")
    
    def has_context(self):
        """
        Check if there is any saved context.
        
        Returns:
            bool: True if context exists, False otherwise
        
        Use case:
            Before trying to use context for follow-up questions.
        """
        return (self.last_intent is not None or 
                self.last_food is not None or 
                len(self.history) > 0)
    
    def update_context(self, **kwargs):
        """
        Update specific context fields.
        
        Args:
            **kwargs: Key-value pairs to update
        
        Example:
            context.update_context(last_food="chicken", custom_field="value")
        """
        for key, value in kwargs.items():
            setattr(self, key, value)
        print(f"📝 Context updated: {kwargs}")
    
    def get_session_duration(self):
        """
        Get how long the current session has been active.
        
        Returns:
            str: Human-readable duration
        
        Use case:
            Analytics or deciding when to clear old context.
        """
        duration = datetime.now() - self.session_start
        minutes = int(duration.total_seconds() / 60)
        return f"{minutes} minutes"

# ============================================
# GLOBAL CONTEXT STORE (for multi-user)
# ============================================
# In production, use Redis or database-backed session storage
# For now, use a simple dictionary keyed by session_id

class GlobalContextStore:
    """
    Manages contexts for multiple users/sessions.
    
    In a real application, each user would have their own session_id,
    and this store would maintain separate contexts for each.
    
    For this demo, we use a single global context, but this class
    shows how to scale to multi-user.
    """
    
    def __init__(self):
        """Initialize the global context store."""
        self.contexts = {}  # Dictionary: session_id -> ChatContext
        print("🌐 Global context store initialized")
    
    def get_context(self, session_id="default"):
        """
        Get or create context for a session.
        
        Args:
            session_id (str): Unique session identifier
        
        Returns:
            ChatContext: Context object for this session
        """
        # If context doesn't exist for this session, create it
        if session_id not in self.contexts:
            self.contexts[session_id] = ChatContext()
            print(f"🆕 Created new context for session: {session_id}")
        
        return self.contexts[session_id]
    
    def clear_context(self, session_id):
        """
        Clear context for a specific session.
        
        Args:
            session_id (str): Session to clear
        """
        if session_id in self.contexts:
            del self.contexts[session_id]
            print(f"🗑️  Cleared context for session: {session_id}")
    
    def cleanup_old_sessions(self, max_age_minutes=30):
        """
        Remove contexts for sessions that haven't been active recently.
        
        Args:
            max_age_minutes (int): Age threshold in minutes
        
        Use case:
            Periodic cleanup to free memory from inactive sessions.
        """
        now = datetime.now()
        to_remove = []
        
        for session_id, context in self.contexts.items():
            age = (now - context.session_start).total_seconds() / 60
            if age > max_age_minutes:
                to_remove.append(session_id)
        
        for session_id in to_remove:
            self.clear_context(session_id)
        
        if to_remove:
            print(f"🧹 Cleaned up {len(to_remove)} old sessions")

# ============================================
# TESTING (run this file directly to test)
# ============================================
if __name__ == "__main__":
    print("Testing Context Manager:")
    print("="*60)
    
    # Test single context
    print("\n1. Testing Single Context:")
    context = ChatContext()
    
    # Save first interaction
    context.save_context("food_query", {"last_food": "banana"})
    print(f"Last context: {context.get_last_context()}")
    
    # Save second interaction
    context.save_context("food_query", {"last_food": "paneer"})
    print(f"Updated context: {context.get_last_context()}")
    
    # Get history
    print(f"\nConversation history: {context.get_history()}")
    
    # Test global store
    print("\n2. Testing Global Context Store:")
    store = GlobalContextStore()
    
    # Get context for user 1
    ctx1 = store.get_context("user_1")
    ctx1.save_context("greeting", {})
    
    # Get context for user 2
    ctx2 = store.get_context("user_2")
    ctx2.save_context("food_query", {"last_food": "apple"})
    
    print(f"User 1 context: {ctx1.get_last_context()}")
    print(f"User 2 context: {ctx2.get_last_context()}")
    
    print("\n" + "="*60)