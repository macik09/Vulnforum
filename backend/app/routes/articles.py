
from flask import Blueprint

articles_bp = Blueprint('articles', __name__, url_prefix='/api/articles')

@articles_bp.route('/', methods=['GET'])
def get_articles():
    return {"articles": []}
