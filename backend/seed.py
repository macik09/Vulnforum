import json
from app.models import User, Article, UnlockedArticle, Message, Comment
from app import db
from datetime import datetime

def seed_database():
    with open('data.json', 'r', encoding='utf-8') as f:
        data = json.load(f)

    db.drop_all()
    db.create_all()

    
    users_map = {}
    for u in data.get('users', []):
        user = User(username=u['username'], password=u['password'], role=u['role'], wallet_balance=u['wallet_balance'])
        db.session.add(user)
        users_map[user.username] = user
    db.session.commit()

    # Dodaj artykuły
    articles_map = {}
    for a in data.get('articles', []):
        article = Article(title=a['title'], content=a['content'], is_paid=a['is_paid'])
        db.session.add(article)
        articles_map[article.title] = article
    db.session.commit()

    # Odblokowania artykułów
    for ua in data.get('unlocked_articles', []):
        user = users_map.get(ua['username'])
        article = articles_map.get(ua['article_title'])
        if user and article:
            unlocked = UnlockedArticle(user_id=user.id, article_id=article.id)
            db.session.add(unlocked)
    db.session.commit()

    # Wiadomości
    for m in data.get('messages', []):
        sender = users_map.get(m['sender'])
        receiver = users_map.get(m['receiver'])
        if sender and receiver:
            message = Message(sender_id=sender.id, receiver_id=receiver.id, content=m['content'], timestamp=datetime.utcnow())
            db.session.add(message)
    db.session.commit()

    # Komentarze
    for c in data.get('comments', []):
        article = articles_map.get(c['article_title'])
        if article:
            comment = Comment(text=c['text'], file_path=c['file_path'], article_id=article.id)
            db.session.add(comment)
    db.session.commit()
