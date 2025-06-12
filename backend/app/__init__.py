from flask import Flask
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from flask_jwt_extended import JWTManager

db = SQLAlchemy()

def create_app():
    app = Flask(__name__)
    CORS(app)

    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data.db'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    app.config["JWT_SECRET_KEY"] = "super-secret"  # wymagana konfiguracja JWT
    app.config["JWT_TOKEN_LOCATION"] = ["headers"]  # domyślnie token w nagłówkach Authorization

    jwt = JWTManager(app)

    db.init_app(app)

    # Rejestracja blueprintów
    from app.routes.auth import auth_bp
    from app.routes.articles import articles_bp
    from app.routes.messages import messages_bp
    from app.routes.wallet import wallet_bp
    from app.routes.admin import admin_bp
    

    app.register_blueprint(auth_bp)
    app.register_blueprint(articles_bp)
    app.register_blueprint(messages_bp)
    app.register_blueprint(wallet_bp)
    app.register_blueprint(admin_bp)


    with app.app_context():
        db.create_all()

    return app
