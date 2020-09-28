#include <connectionHandler.h>

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

ConnectionHandler::ConnectionHandler(string host, short port, MessagingProtocolSTOMP &protocolStomp, std::mutex &mutex): host_(host), port_(port), io_service_(), socket_(io_service_), protocolStomp(protocolStomp), mutex(mutex) {}

ConnectionHandler::~ConnectionHandler() {
    close();
}

bool ConnectionHandler::connect() {
    std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
            tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\0');
}

bool ConnectionHandler::sendLineKeyboard(std::string& line) {
    string answer = protocolStomp.processFromKeyboard(line);
    if(answer == "")
        return true;
    return sendFrameAscii(answer);
}

bool ConnectionHandler::sendLineServer(std::string &line) {
    Frame frame = createFrame(line);
    string answer = protocolStomp.processFromServer(frame);
    if(answer == "")
        return true;
    return sendFrameAscii(answer);
}

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {
        do{
            if(!getBytes(&ch, 1))
            {
                return false;
            }
            if(ch!='\0')
                frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        std::cerr << "recv failed2 (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}


bool ConnectionHandler::sendFrameAscii(const std::string& frame) {
    std::lock_guard<std::mutex> lock(mutex);
    return sendBytes(frame.c_str(),frame.length());
}

void ConnectionHandler::split(const string &txt, vector<string> &strs) {
    size_t pos = txt.find('\n');
    size_t initialPos = 0;
    strs.clear();
    while( pos != std::string::npos ) {
        strs.push_back( txt.substr( initialPos, pos - initialPos ) );
        initialPos = pos + 1;
        pos = txt.find('\n', initialPos );
    }
    strs.push_back( txt.substr( initialPos, std::min( pos, txt.size() ) - initialPos + 1 ) );
}

Frame ConnectionHandler::createFrame(string s) {
    vector<string> lines;
    split(s, lines);
    string stompCommand = lines.at(0);
    map<string, string> headers;
    string bodyFrame;
    unsigned int index = 1;
    while(index < lines.size() && !(lines.at(index).size() == 0)) {
        int pos = lines.at(index).find(":");
        string key = lines.at(index).substr(0, pos);
        string value = lines.at(index).substr(pos + 1);
        headers.insert(pair<string,string>(key, value));
        index++;
    }
    index++;
    while (lines.at(index) != "\0") {
        bodyFrame += lines.at(index) + '\n';
        index++;
    }
    Frame frame(stompCommand, headers, bodyFrame);
    return frame;
}

bool ConnectionHandler::isShouldTerminate() { return this->protocolStomp.getShouldTerminate(); }

bool ConnectionHandler::isTerminate() { return this->protocolStomp.getTerminate(); }

bool ConnectionHandler::isError() { return this->protocolStomp.getError(); }

// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}