import jwt
import datetime

SECRET = "supersecretkey"

def generate_token(user):
    payload = {
        "sub": user["id"],
        "username": user["username"],
        "role": user["role"],
        "exp": datetime.datetime.utcnow() + datetime.timedelta(hours=2)
    }
    return jwt.encode(payload, SECRET, algorithm="HS256")
