//
// Created by royek on 11/01/2020.
//

#ifndef BOOST_ECHO_CLIENT_CLIENT_H
#define BOOST_ECHO_CLIENT_CLIENT_H

#include <string>
#include <map>
#include <vector>
#include "../include/Book.h"
class Book;

using namespace std;

class Client {

private:
    string name;
    int subscriptionId;
    map<Book*,bool> clientBooks;
    vector<string> booksToBorrow;
    map<string,int> topicSubscriptions;

public:
    Client(string name);
    ~Client();
    void addBook(string bookName, string topic, string bookLender);
    void removeBook(string bookName);
    void returnBook(string bookName);
    string getName();
    vector<Book*> getBooks();
    Book* getBook(string bookName);
    int getSubscriptionId();
    void addBookToBorrow(string bookName, string topic);
    void removeBookToBorrow(string bookName);
    bool wantToBorrow(string bookName);
    void addTopicSubscription(string topic, int subscriptionId);
    void removeTopicSubscription(string topic);
    int getTopicSubscription(string topic);

};


#endif //BOOST_ECHO_CLIENT_CLIENT_H
