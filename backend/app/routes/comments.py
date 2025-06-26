
import os
from flask import Blueprint, request, jsonify, current_app
from werkzeug.utils import secure_filename
from app.models.models import db, Comment, Article

comments_bp = Blueprint('comments', __name__, url_prefix='/api')

import os
from flask import Blueprint, request, jsonify, current_app, make_response
from werkzeug.utils import secure_filename
from app.models.models import db, Comment, Article

comments_bp = Blueprint('comments', __name__, url_prefix='/api')

@comments_bp.route("/articles/<int:article_id>/comments", methods=["POST", "OPTIONS"])
def add_comment(article_id):
    if request.method == "OPTIONS":
        response = make_response()
        response.headers.add("Access-Control-Allow-Origin", "*")
        response.headers.add("Access-Control-Allow-Methods", "POST, OPTIONS")
        response.headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization") 
        return response

    if "text" not in request.form:
        return jsonify({"error": "Brakuje pola tekstowego"}), 400

    text = request.form["text"]
    article = Article.query.get_or_404(article_id)

    file = request.files.get("file")
    file_path = None

    if file:
        filename = secure_filename(file.filename) 
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

    response = jsonify({"message": "Komentarz dodany"}), 201
    response[0].headers.add("Access-Control-Allow-Origin", "*")
    return response

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

