from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required
from app.models.models import db, Article, User, UnlockedArticle

admin_bp = Blueprint('admin', __name__, url_prefix='/api/admin')

@admin_bp.route('/articles/<int:article_id>', methods=['PATCH'])
@jwt_required()
def update_article(article_id):
    article = Article.query.get(article_id)
    if not article:
        return jsonify({"error": "Nie znaleziono artykułu"}), 404

    data = request.get_json()
    is_paid = data.get("is_paid")

    if is_paid is None:
        return jsonify({"error": "Brak wartości is_paid"}), 400

    article.is_paid = bool(is_paid)
    db.session.commit()

    return jsonify({"message": "Zaktualizowano artykuł", "is_paid": article.is_paid}), 200

@admin_bp.route('/users', methods=['GET'])
@jwt_required()
def get_all_users():
    users = User.query.all()
    result = []
    for user in users:
        result.append({
            "id": user.id,
            "username": user.username,
            "balance": user.wallet_balance,
            "unlocked_articles": [ua.article_id for ua in user.unlocked_articles]
        })
    return jsonify(result), 200
