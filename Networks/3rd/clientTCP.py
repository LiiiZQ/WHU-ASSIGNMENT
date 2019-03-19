from socket import *

serverName = 'localhost'
serverPort = 18601
clientSocket = socket(AF_INET, SOCK_STREAM)
clientSocket.connect((serverName, serverPort))

num = input("Please input a number\n")
clientSocket.send(num.encode())
result = clientSocket.recv(1024).decode()
print('[From Server]The square of the number is:', result)


clientSocket.close()


