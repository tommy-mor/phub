import cherrypy

class LoginServer(object):
    def index(self):
        return "Hello World!"
    index.exposed = True

cherrypy.quickstart(LoginServer())
