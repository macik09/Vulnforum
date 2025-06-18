from app import create_app, db
from seed import seed_database  

app = create_app()

with app.app_context():
    db.create_all()
    seed_database()  

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
