//
// Created by sagiv on 1/12/20.
//

#include <iostream>
#include "MessagingProtocolSTOMP.h"

MessagingProtocolSTOMP::MessagingProtocolSTOMP(Client &client): client(client), reciptsInfo() {}

void MessagingProtocolSTOMP::split(const string &txt, vector<string> &strs) {
    size_t pos = txt.find(" ");
    size_t initialPos = 0;
    strs.clear();
    while( pos != std::string::npos ) {
        strs.push_back( txt.substr( initialPos, pos - initialPos ) );
        initialPos = pos + 1;
        pos = txt.find(" ", initialPos );
    }
    strs.push_back( txt.substr( initialPos, std::min( pos, txt.size() ) - initialPos + 1 ) );
}

string MessagingProtocolSTOMP::processFromKeyboard(string keyboardLine) {
    vector<string> strs;
    split(keyboardLine, strs);
    string frame;
    if ((strs.size() == 4) && (strs.at(0) == "login")) {
        frame = "CONNECT\n";
        frame += "accept-version:1.2\n";
        frame += "host:stomp.cs.bgu.ac.il\n";
        frame += "login:" + strs.at(2) + '\n';
        frame += "passcode:" + strs.at(3) + '\n' + "\n" + '\0';
        return frame;
    }
    else if (strs.size() == 2 && strs.at(0) == "join") {
        client.addTopicSubscription(strs.at(1), client.getSubscriptionId());
        int reciptId = Frame::getFrameId();
        reciptsInfo.insert({reciptId, "Joined club " + strs.at(1)});
        frame = "SUBSCRIBE\n";
        frame += "destination:" + strs.at(1) + '\n';
        frame += "id:";
        frame += to_string(client.getTopicSubscription(strs.at(1))) + '\n';
        frame += "receipt:";
        frame += to_string(reciptId) + '\n';
        frame += "\n";
        frame += '\0';
        return frame;
    }
    else if(strs.size() == 2 && strs.at(0) == "exit") {
        int reciptId = Frame::getFrameId();
        reciptsInfo.insert({reciptId, "Exited club " + strs.at(1)});
        frame = "UNSUBSCRIBE\n";
        frame += "id:";
        frame += to_string(client.getTopicSubscription(strs.at(1))) + '\n';
        frame += "receipt:";
        frame += to_string(reciptId) + '\n';
        frame += "\n";
        frame += '\0';
        client.removeTopicSubscription(strs.at(1));
        return frame;
    }
    else if (strs.size() >= 3 && strs.at(0) == "add") {
        string topic = strs.at(1);
        string bookName = strs.at(2);
        for (unsigned int i = 3; i < strs.size(); ++i)
            bookName += " " + strs.at(i);
        frame = "SEND\n";
        frame += "destination:" + strs.at(1) + '\n' + "\n";
        frame += client.getName() + " has added the book " + bookName + '\n';
        frame += '\0';
        client.addBook(bookName, topic, "");
        return frame;
    } else if (strs.size() >= 3 && strs.at(0) == "borrow") {
        string bookName = strs.at(2);
        for (unsigned int i = 3; i < strs.size(); ++i)
            bookName += " " + strs.at(i);
        client.addBookToBorrow(bookName, strs.at(1));
        frame = "SEND\n";
        frame += "destination:" + strs.at(1) + '\n' + "\n";
        frame += client.getName() + " wish to borrow " + bookName + '\n';
        frame += '\0';
        return frame;
    } else if (strs.size() >= 3 && strs.at(0) == "return") {
        string bookName = strs.at(2);
        for (unsigned int i = 3; i < strs.size(); ++i)
            bookName += " " + strs.at(i);
        frame = "SEND\n";
        frame += "destination:" + strs.at(1) + '\n' + "\n";
        frame += "Returning " + bookName + " to " + client.getBook(bookName)->getBookLender() + '\n';
        frame += '\0';
        client.removeBook(bookName);
        return frame;
    } else if (strs.size() == 2 && strs.at(0) == "status") {
        frame = "SEND\n";
        frame += "destination:" + strs.at(1) + '\n';
        frame += "\n";
        frame += "book status";
        frame += '\n';
        frame += '\0';
        return frame;
    } else if (strs.size() == 1 && strs.at(0) == "logout") {
        shouldTerminate = true;
        frame = "DISCONNECT\n";
        frame += "receipt:" + to_string(Frame::getFrameId()) + '\n';
        frame += "\n";
        frame += '\0';
        return frame;
    }
    return "";
}

string MessagingProtocolSTOMP::processFromServer(Frame &message) {
    string stompCommand = message.getStompCommand();
    map<string, string> headers = message.getHeaders();
    string frameBody = message.getFrameBody();

    if(stompCommand == "MESSAGE") {
        vector<string> strs;
        split(frameBody, strs);
        if(frameBody.find("wish to borrow") < frameBody.length()) {
            string clientName = strs.at(0);
            string topic = headers.at("destination");
            string bookName = strs.at(4);
            for (unsigned int i = 5; i < strs.size(); ++i)
                bookName += " " + strs.at(i);
            bookName = bookName.substr(0, bookName.size() - 1);
            if(clientName != client.getName()) {
                Book *book = client.getBook(bookName);
                if (book->getBookName() != "notExist") {
                    string haveBook = "SEND\n";
                    haveBook += "destination:" + topic + '\n' + "\n";
                    haveBook += client.getName() + " has " + bookName + '\n';
                    haveBook += '\0';
                    return haveBook;
                } else { delete book; }
                return "";
            }
            return "";
        }
        else if(frameBody.find("has") < frameBody.length()) {
            string topic = headers.at("destination");
            string bookOwner = strs.at(0);
            string bookName = strs.at(2);
            for (unsigned int i = 3; i < strs.size(); ++i)
                bookName += " " + strs.at(i);
            bookName = bookName.substr(0, bookName.size() - 1);
            if(client.wantToBorrow(bookName)){
                client.removeBookToBorrow(bookName);
                client.addBook(bookName, topic, bookOwner);
                string takeBook = "SEND\n";
                takeBook += "destination:" + topic + '\n' + "\n";
                takeBook += "Taking " + bookName + " from " + bookOwner + '\n';
                takeBook += '\0';
                return takeBook;
            }
            return "";
        }
        else if(frameBody.find("Taking") < frameBody.length()) {
            string bookName = strs.at(1);
            for (unsigned int i = 2; i < strs.size() - 2; ++i)
                bookName += " " + strs.at(i);
            string bookOwner = strs.at(strs.size() - 1);
            bookOwner = bookOwner.substr(0, bookOwner.size() - 1);
            if(bookOwner == client.getName())
                client.removeBook(bookName);
            return "";
        }
        else if(frameBody.find("Returning") < frameBody.length()) {
            string clientName = strs.at(strs.size() - 1);
            clientName = clientName.substr(0, clientName.size() - 1);
            string bookName = strs.at(1);
            for (unsigned int i = 2; i < strs.size() - 2; ++i)
                bookName += " " + strs.at(i);
            if(clientName == client.getName())
                client.returnBook(bookName);
            return "";
        }
        else if(frameBody.find("book status") < frameBody.length()) {
            string topic = headers.at("destination");
            vector<Book*> books = client.getBooks();
            string bookStatus = "SEND\n";
            bookStatus += "destination:" + topic + '\n' + "\n";
            bookStatus += client.getName() + ":";
            if(books.size() > 0)
                bookStatus += books.at(0)->getBookName();
            for (unsigned int i = 1; i < books.size(); ++i)
                bookStatus +=  "," + books.at(i)->getBookName();
            bookStatus += '\n';
            bookStatus += '\0';
            return bookStatus;
        }
    }

    if(stompCommand == "CONNECTED") {
        cout<<"Login successful"<<endl;
        return "";
    }

    if(stompCommand == "ERROR") {
        shouldTerminate = true;
        terminate = true;
        error=true;
        string errorValue = headers.at("message");
        if(errorValue == "Wrong password")
            cout<<"Wrong password\nan ERROR has accured please enter the word bye to logout"<<endl;
        else if(errorValue == "User already logged in")
            cout<<"User already logged in\nan ERROR has accured please enter the word bye to logout"<<endl;
        return "";
    }

    if(stompCommand == "RECEIPT") {
        if(shouldTerminate) {
            terminate = true;
            return "";
        }
        else {
            int reciptId = stoi(headers.at("receipt-id"));
            for (map<int,string>::iterator it = reciptsInfo.begin() ; it != reciptsInfo.end(); ++it) {
                if(it->first == reciptId) {
                    cout<<it->second<<endl;
                    reciptsInfo.erase(it);
                    break;
                }
            }
        }
    }

    return "";
}

bool MessagingProtocolSTOMP::getShouldTerminate() { return this->shouldTerminate; }
bool MessagingProtocolSTOMP::getTerminate() { return this->terminate; }
bool MessagingProtocolSTOMP::getError() { return this->error; }