
import os
from flask import Blueprint, request, jsonify, current_app
from werkzeug.utils import secure_filename
from app.models.models import db, Comment, Article

comments_bp = Blueprint('comments', __name__, url_prefix='/api')

@comments_bp.route("/articles/<int:article_id>/comments", methods=["POST"])
def add_comment(article_id):
    if "text" not in request.form:
        return jsonify({"error": "Brakuje pola tekstowego"}), 400

    text = request.form["text"]
    article = Article.query.get_or_404(article_id)


    file = request.files.get("file")
    file_path = None

    if file:
        filename = file.filename
        upload_dir = os.path.join(current_app.root_path, "uploads")
        os.makedirs(upload_dir, exist_ok=True)

        save_path = os.path.join(upload_dir, filename)
        file.save(save_path)
        file_path = f"/uploads/{filename}"

    comment = Comment(
        text=text,
        file_path=file_path,
        article_id=article.id
    )
    db.session.add(comment)
    db.session.commit()

    return jsonify({"message": "Komentarz dodany"}), 201

@comments_bp.route("/articles/<int:article_id>/comments", methods=["GET"])
def get_comments(article_id):
    article = Article.query.get_or_404(article_id)
    comments = Comment.query.filter_by(article_id=article.id).all()
    result = [
        {
            "id": c.id,
            "text": c.text,
            "file_path": c.file_path
        } for c in comments
    ]
    return jsonify(result), 200

