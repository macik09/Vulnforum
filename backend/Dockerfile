FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

ENV FLASK_APP=run.py
ENV FLASK_RUN_HOST=0.0.0.0
ENV FLASK_ENV=development

EXPOSE 5000

CMD ["python", "run.py"]
