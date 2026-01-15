#!/bin/bash

# Play Link Startup Script
# Automatically starts MongoDB (if not running) and Spring Boot app

set -e

echo "🚀 Starting Play Link Application..."

# MongoDB configuration
MONGODB_PATH="/Users/yasastennakoon/mongodb-macos-aarch64-7.0.11/bin/mongod"
MONGODB_DATA_PATH="$HOME/data/db"
MONGODB_PORT=27017

# Java configuration
JAVA_VERSION=22

echo ""
echo "📋 Checking prerequisites..."

# Check if MongoDB port is in use
if lsof -Pi :$MONGODB_PORT -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "✅ MongoDB is already running on port $MONGODB_PORT"
else
    echo "⚠️  MongoDB is not running. Starting MongoDB..."
    
    # Create data directory if it doesn't exist
    mkdir -p "$MONGODB_DATA_PATH"
    
    # Start MongoDB in the background
    echo "   Starting: $MONGODB_PATH --dbpath $MONGODB_DATA_PATH"
    nohup "$MONGODB_PATH" --dbpath "$MONGODB_DATA_PATH" > /dev/null 2>&1 &
    
    # Wait for MongoDB to start
    echo "   Waiting for MongoDB to start..."
    sleep 3
    
    if lsof -Pi :$MONGODB_PORT -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "✅ MongoDB started successfully"
    else
        echo "❌ Failed to start MongoDB"
        exit 1
    fi
fi

echo ""
echo "☕ Setting up Java $JAVA_VERSION..."

# Set JAVA_HOME to Java 22
if /usr/libexec/java_home -v $JAVA_VERSION >/dev/null 2>&1; then
    export JAVA_HOME=$(/usr/libexec/java_home -v $JAVA_VERSION)
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "✅ Using Java: $(java -version 2>&1 | head -n 1)"
else
    echo "❌ Java $JAVA_VERSION not found. Please install JDK $JAVA_VERSION"
    exit 1
fi

echo ""
echo "🔧 Building and starting Spring Boot application..."
echo ""

# Run the Spring Boot application
./mvnw spring-boot:run
