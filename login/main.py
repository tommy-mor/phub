import os
import sys
from functools import wraps
from sqlalchemy import Column, ForeignKey, Integer, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from itsdangerous import Signer, BadSignature

from sanic import Sanic
from sanic.response import json, html, redirect

#establish connection to db
engine = create_engine('sqlite:///sqlalchemy_example.db')

Base = declarative_base()

class User(Base):
    __tablename__ = 'person'
    id = Column(Integer, primary_key=True)
    name = Column(String, nullable=False)
    password = Column(String(250), nullable=False)

#only on first run i think
#Base.metadata.create_all(engine)


DBSession = sessionmaker(bind=engine)
session= DBSession()

s = Signer('secret-key')
#start web server to serve the login page

app = Sanic()

def authorized():
    def decorator(f):
        @wraps(f)
        async def decorated_function(request, *args, **kwargs):
            uname = request.cookies.get('username')
            if not uname: uname = 'none'
            try:
                is_authorized = s.unsign(uname)
                response = await f(request, *args, **kwargs)
                return response
            except BadSignature:
                return json({'status': 'not authorized'}, 403)
        return decorated_function
    return decorator

@app.route('/')
async def handle_request(request):
    return html("""<form action=login>
                <input type="text" name=uname required>
                <input type=password name=pword required>
                <button type=submit>login</button>
                </form>""")

@app.route('/login')
async def handle_request(request):
    user = session.query(User).filter(User.name == request.args['uname'][0]).first()
    if user and user.password == request.args['pword'][0]:
        response = redirect('/protected')
        response.cookies['username'] = s.sign(user.name.encode()).decode()
        return response
    else:
        return html('notfound')
    return html(request.args)

@app.route('/logout')
@authorized()
async def handle_request(request):
    response = redirect('/')
    del response.cookies['username']
    return response

@app.route('/protected')
@authorized()
async def handle_request(request):
    return html('i am logged in good job') # return the cljs static site, with usertoken



if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000)
