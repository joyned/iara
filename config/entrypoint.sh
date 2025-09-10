#!/bin/sh

mkdir -p /data

if [ "$JWT_SECRET" = "default_jwt_secret_please_change_in_production_environment" ]; then
    export JWT_SECRET=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9!@#$%^&*()_+-=' | fold -w 50 | head -n 1 | sed 's/./&/g')
    echo "JWT_SECRET aleatório gerado automaticamente"
else
    echo "JWT_SECRET fornecido externamente"
fi

if [ ! -f /data/iara.db ]; then
    echo "Criando novo banco de dados SQLite..."
    sqlite3 /data/iara.db "VACUUM;"
fi

chmod 666 /data/iara.db 2>/dev/null || true

export SQLITE_DB_PATH="jdbc:sqlite:/data/iara.db"
export DB_PATH="/data/iara.db"

echo "Usando banco de dados: $DB_PATH"
echo "JDBC URL: $SQLITE_DB_PATH"
echo "JWT Secret: ${JWT_SECRET:0:10}..."

java -jar /server/iara-server.jar &

cd /ui

serve -l 5173 -s &

nginx -g 'daemon off;'