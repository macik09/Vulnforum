import jwt
import datetime

SECRET = "supersecretkey"

def generate_token(user):
    payload = {
        "sub": str(user["id"]),   
        "username": user["username"],
        "role": user["role"],
        "exp": datetime.datetime.utcnow() + datetime.timedelta(days=1)
    }
    token = jwt.encode(payload, SECRET, algorithm="HS256")

    if isinstance(token, bytes):
        return token.decode("utf-8")
    return token
