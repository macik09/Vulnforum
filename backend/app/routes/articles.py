from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models.models import db, Article, User

articles_bp = Blueprint('articles', __name__, url_prefix='/api/articles')


@articles_bp.route('/', methods=['GET'])
@jwt_required()
def get_articles():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)

    articles = Article.query.all()
    return jsonify([
        {
            "id": a.id,
            "title": a.title,
            "content": a.content if not a.is_paid or user.wallet_balance >= 3 else "Zablokowana treść — zapłać",
            "is_paid": a.is_paid
        } for a in articles
    ])

# Odblokuj artykuł
@articles_bp.route('/unlock/<int:article_id>', methods=['POST'])
@jwt_required()
def unlock_article(article_id):
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    article = Article.query.get(article_id)

    if not article:
        return jsonify({"error": "Artykuł nie istnieje"}), 404

    if not article.is_paid:
        return jsonify({"message": "Artykuł już jest darmowy"})

    if user.wallet_balance < 3:
        return jsonify({"error": "Za mało vulndolców"}), 403

    # odejmij środki
    user.wallet_balance -= 3
    db.session.commit()

    return jsonify({"message": f"Odblokowano artykuł {article.title}"}), 200
