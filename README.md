# Account Ledger 2.0

A Java-based financial application designed to help users track and manage their personal or business transactions. This project was developed as part of the [Pluralsight](https://github.com/keishdlr/AccountLedger2.0/tree/main/src/main/java/com/pluralsight) curriculum to demonstrate proficiency in object-oriented programming and file I/O operations.

## 🚀 Features

* **Transaction Tracking:** Record deposits and payments with details like date, time, and description.
* **Ledger Management:** View all entries, only deposits, or only payments.
* **Custom Reports:** Filter transactions by month, year, or specific vendor.
* **Data Persistence:** Automatically saves and loads data from a CSV file.

## 🛠️ Tech Stack

* **Language:** [Java](https://github.com/keishdlr/AccountLedger2.0)
* **Build Tool:** [Maven](https://github.com/keishdlr/AccountLedger2.0/blob/main/pom.xml)
* **Concepts:** File I/O, Collections (ArrayList/HashMap), and Date/Time API.

## 📋 Prerequisites

* [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) 17 or higher.
* [Apache Maven](https://maven.apache.org/download.cgi).

## 🔧 Installation & Setup

1. **Clone the repository:**
```bash
git clone https://github.com/keishdlr/AccountLedger2.0.git

```


2. **Navigate to the project directory:**
```bash
cd AccountLedger2.0

```


3. **Build the project:**
```bash
mvn clean install

```


4. **Run the application:**
```bash
mvn exec:java -Dexec.mainClass="com.pluralsight.AccountLedgerApp"

```



## 📂 Project Structure

* `src/main/java/com/pluralsight`: Contains the core Java source code.
* `pom.xml`: Defines dependencies and build configurations.
* `transactions.csv`: (Generated) The data file where all ledger entries are stored.
