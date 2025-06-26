from flask import Blueprint, jsonify, request, make_response
from sqlalchemy import text
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models.models import db, Article, User, UnlockedArticle
from app.routes.wallet import SECRET_UNLOCK_KEY

articles_bp = Blueprint('articles', __name__, url_prefix='/api/articles')


@articles_bp.route('/', methods=['GET'])
@jwt_required()
def get_articles():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)

    articles = Article.query.all()

    for a in articles:
        raw_val = db.session.execute(
            text(f"SELECT is_paid FROM article WHERE id={a.id}")
        ).scalar()
        is_unlocked = UnlockedArticle.query.filter_by(user_id=user_id, article_id=a.id).first() is not None

    return jsonify([
    {
        "id": a.id,
        "title": a.title,
        "content": a.content if not a.is_paid or UnlockedArticle.query.filter_by(user_id=user_id, article_id=a.id).first() else "Zablokowana treść — zapłać",
        "is_paid": a.is_paid,
        "is_unlocked": not a.is_paid or UnlockedArticle.query.filter_by(user_id=user_id, article_id=a.id).first() is not None
    } for a in articles
])


@articles_bp.route('/unlock/<int:article_id>', methods=['POST'])
@jwt_required()
def unlock_article(article_id):
   
    provided_access_key = request.headers.get('X-Access-Key')
    

    if not provided_access_key or provided_access_key != SECRET_UNLOCK_KEY:
        return jsonify({"error": "Brak lub nieprawidłowy klucz dostępu. Musisz najpierw wykonać 'zakup' w portfelu."}), 400

    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    article = Article.query.get(article_id)

    if not article:
        return jsonify({"error": "Artykuł nie istnieje"}), 404

    if not article.is_paid:
        return jsonify({"message": "Artykuł już jest darmowy"})

    already_unlocked = UnlockedArticle.query.filter_by(
        user_id=user_id, article_id=article_id
    ).first()
    if already_unlocked:
        return jsonify({"message": "Artykuł już odblokowany"})

 
    article_price = 5 
    if user.wallet_balance < article_price:
        return jsonify({"error": "Za mało vulndolców"}), 403

    user.wallet_balance -= article_price
    unlock = UnlockedArticle(user_id=user_id, article_id=article_id)
    db.session.add(unlock)
    db.session.commit()

    return jsonify({"message": f"Odblokowano artykuł {article.title}"}), 200
