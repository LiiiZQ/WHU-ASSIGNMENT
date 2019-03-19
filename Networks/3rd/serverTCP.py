from socket import *


serverPort = 18601
serverSocket = socket(AF_INET, SOCK_STREAM)
serverSocket.bind(('', serverPort))
serverSocket.listen(1)

print('The server is ready')

while True:
    connectionSocket, addr = serverSocket.accept()

    input = int(connectionSocket.recv(1024).decode())
    result = input * input
    connectionSocket.send(str(result).encode())


    connectionSocket.close()







