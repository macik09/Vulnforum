import jwt
import datetime

SECRET = "supersecretkey"

def generate_token(user):
    payload = {
        "sub": str(user["id"]),   # <-- tutaj rzutujesz na string
        "username": user["username"],
        "role": user["role"],
        "exp": datetime.datetime.utcnow() + datetime.timedelta(hours=2)
    }
    token = jwt.encode(payload, SECRET, algorithm="HS256")

    if isinstance(token, bytes):
        return token.decode("utf-8")
    return token
