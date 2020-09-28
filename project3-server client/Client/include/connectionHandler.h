
#ifndef CONNECTION_HANDLER__
#define CONNECTION_HANDLER__

#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <mutex>
#include "MessagingProtocolSTOMP.h"
#include "Frame.h"

using boost::asio::ip::tcp;

class MessagingProtocolSTOMP;
class Frame;

class ConnectionHandler {
private:
    const std::string host_;
    const short port_;
    boost::asio::io_service io_service_;   // Provides core I/O functionality
    tcp::socket socket_;
    MessagingProtocolSTOMP protocolStomp;
    std::mutex &mutex;

    Frame createFrame(string s);
    void split(const string &txt, vector<string> &strs);


public:
    ConnectionHandler(std::string host, short port, MessagingProtocolSTOMP &protocolStomp, std::mutex &mutex);

    virtual ~ConnectionHandler();

    // Connect to the remote machine
    bool connect();

    // Read a fixed number of bytes from the server - blocking.
    // Returns false in case the connection is closed before bytesToRead bytes can be read.
    bool getBytes(char bytes[], unsigned int bytesToRead);

    // Send a fixed number of bytes from the client - blocking.
    // Returns false in case the connection is closed before all the data is sent.
    bool sendBytes(const char bytes[], int bytesToWrite);

    // Read an ascii line from the server
    // Returns false in case connection closed before a newline can be read.
    bool getLine(std::string& line);

    bool sendLineKeyboard(std::string& line);

    bool sendLineServer(std::string& line);

    // Get Ascii data from the server until the delimiter character
    // Returns false in case connection closed before null can be read.
    bool getFrameAscii(std::string& frame, char delimiter);

    // Send a message to the remote host.
    // Returns false in case connection is closed before all the data is sent.
    bool sendFrameAscii(const std::string& frame);

    bool isShouldTerminate();

    bool isTerminate();

    bool isError();

    // Close down the connection properly.
    void close();

}; //class ConnectionHandler

#endif