//
// Created by royek on 11/01/2020.
//

#include "Client.h"
#include "Book.h"

using namespace std;

Client::Client(string name):name(name), subscriptionId(0), clientBooks(), booksToBorrow(), topicSubscriptions() {}

void Client::addBook(string bookName, string topic, string bookLender) {
    Book *book = new Book(bookName, topic, bookLender);
    clientBooks.insert({book, true});
}

void Client::removeBook(string bookName) {
    for (map<Book*,bool>::iterator it = clientBooks.begin() ; it != clientBooks.end(); ++it) {
        if((it->first->getBookName() == bookName) & (it->second)){
            it->second = false;
            break;
        }
    }
}

void Client::returnBook(string bookName) {
    for (map<Book*,bool>::iterator it = clientBooks.begin() ; it != clientBooks.end(); ++it) {
        if((it->first->getBookName() == bookName) & (!(it->second))){
            it->second = true;
            break;
        }
    }
}

Book* Client::getBook(string bookName) {
    for (map<Book*,bool>::iterator it = clientBooks.begin() ; it != clientBooks.end(); ++it) {
        if(it->first->getBookName()== bookName && it->second)
            return it->first;
    }
    Book *book = new Book("notExist", "notExist", "notExist");
    return book;
}

int Client::getSubscriptionId() { return subscriptionId++; }
string Client::getName() { return this->name; }

vector<Book*> Client::getBooks() {
    vector<Book *> myBooks;
    for (map<Book *, bool>::iterator it = clientBooks.begin(); it != clientBooks.end(); ++it) {
        if (it->second)
            myBooks.push_back(it->first);
    }
    return myBooks;
}

void Client::addBookToBorrow(string bookName, string topic) {
    for (map<string, int>::iterator it = topicSubscriptions.begin(); it != topicSubscriptions.end(); ++it) {
        if (it->first.compare(topic) == 0)
            this->booksToBorrow.push_back(bookName);
    }
}

void Client::removeBookToBorrow(string bookName) {
    vector<string>::iterator it = booksToBorrow.begin();
    for (unsigned int i = 0; i < booksToBorrow.size(); ++i) {
        if(*it == bookName) {
            booksToBorrow.erase(it);
            break;
        } else { it++; }
    }
}

bool Client::wantToBorrow(string bookName) {
    vector<string>::iterator it = booksToBorrow.begin();
    for (unsigned int i = 0; i < booksToBorrow.size(); ++i) {
        if(*it == bookName) { return true; }
        else { it++; }
    }
    return false;
}

void Client::addTopicSubscription(string topic, int subscriptionId) {
    this->topicSubscriptions.insert({topic, subscriptionId});
}

void Client::removeTopicSubscription(string topic) {
    this->topicSubscriptions.erase(topic);
}

int Client::getTopicSubscription(string topic) {
    return topicSubscriptions.at(topic);
}

Client::~Client() {
    for (map<Book *, bool>::iterator it = clientBooks.begin(); it != clientBooks.end(); ++it)
        delete it->first;
    clientBooks.clear();
}