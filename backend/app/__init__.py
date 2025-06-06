from flask import Flask
from flask_cors import CORS

def create_app():
    app = Flask(__name__)
    CORS(app)

    from app.routes.auth import auth_bp
    from app.routes.articles import articles_bp
    from app.routes.messages import messages_bp

    app.register_blueprint(auth_bp)
    app.register_blueprint(articles_bp)
    app.register_blueprint(messages_bp)

    return app
