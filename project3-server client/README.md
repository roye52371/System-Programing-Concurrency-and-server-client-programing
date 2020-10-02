This project demonstrate a Book Club.
Users will be able to signup for reading clubs and borrow books from each other.
clients sends different request(borrow,return books, joins different book clubs and etc) to the server which get the requests,
and do things accordingly and return message to the client/s).
Implemented both a server, which provide STOMP server services, and a client, which a user can use
in order to interact with the rest of the users.
The server (implemented in java) support both Thread-Per-Client (TPS) and the Reactor, choosing which one according to arguments given on startup.
The client implemented in C++ .
All communication between the clients and the server will be according to the STOMP `Simple-Text-Oriented Messaging-Protocol' protocol.
*There are also few examples for different protocols and clients.
