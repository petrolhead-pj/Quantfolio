#!/usr/bin/env bash
# ============================================================
#  Quantfolio — Quick Setup Script
# ============================================================
set -e

echo "=============================="
echo "  Quantfolio Setup"
echo "=============================="

# 1. Check Java
java -version 2>&1 | head -1 || { echo "ERROR: Java not found. Install Java 11+"; exit 1; }

# 2. Check Maven
mvn -version | head -1 || { echo "ERROR: Maven not found. Install Maven 3.6+"; exit 1; }

# 3. Prompt for DB credentials
read -p "MySQL host [localhost]: " DB_HOST; DB_HOST=${DB_HOST:-localhost}
read -p "MySQL user [root]: "     DB_USER; DB_USER=${DB_USER:-root}
read -s -p "MySQL password: "     DB_PASS; echo ""

# 4. Patch DBConnection
sed -i "s|jdbc:mysql://localhost:3306|jdbc:mysql://${DB_HOST}:3306|" \
    src/main/java/com/quantfolio/util/DBConnection.java
sed -i "s|DB_USER = \"root\"|DB_USER = \"${DB_USER}\"|" \
    src/main/java/com/quantfolio/util/DBConnection.java
sed -i "s|DB_PASS = \"your_password_here\"|DB_PASS = \"${DB_PASS}\"|" \
    src/main/java/com/quantfolio/util/DBConnection.java

# 5. Import DB
echo "Importing database schema and seed data..."
mysql -u"${DB_USER}" -p"${DB_PASS}" -h"${DB_HOST}" < db/schema.sql
mysql -u"${DB_USER}" -p"${DB_PASS}" -h"${DB_HOST}" quantfolio < db/seed_data.sql
mysql -u"${DB_USER}" -p"${DB_PASS}" -h"${DB_HOST}" quantfolio < db/triggers.sql
mysql -u"${DB_USER}" -p"${DB_PASS}" -h"${DB_HOST}" quantfolio < db/views.sql

# 6. Build
echo "Building WAR file..."
mvn clean package -q

echo ""
echo "=============================="
echo "  Build complete!"
echo "  Deploy target/quantfolio.war to Tomcat webapps/"
echo "  Then open: http://localhost:8080/quantfolio"
echo "=============================="
