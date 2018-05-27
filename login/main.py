import os
import sys
from sqlalchemy import Column, ForeignKey, Integer, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker


from sanic import Sanic
from sanic.response import json,html

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
#start web server to serve the login page

app = Sanic()

@app.route('/')
async def handle_request(request):
    return html("""<form action=login>
<input type=text name=uname>
<input type=password name=pword>
<button type=submit>login</button>
    </form>""")

@app.route('/login')
async def handle_request(request):
    user = session.query(User).filter(User.name == request.args['uname'][0]).first()
    if user.password == request.args['pword'][0]:
        return html('found' + user)
    else:
        return html('notfound')
    return html(request.args)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000)
