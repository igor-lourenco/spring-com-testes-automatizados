
# Na tela do JENKINS, vá em:
# Configurar -> Passos de construção -> Adicionar passo na construção -> Executar shell

#!/bin/bash
set -e

APP_HOME="/opt/spring-com-testes-automatizados"
APP_JAR="$APP_HOME/bin/testes-0.0.1-SNAPSHOT.jar"
APP_LOG="$APP_HOME/logs/app.log"
APP_PORT="9200"

echo "Iniciando build da aplicação..."

# >>> Não esquecer de rodar o comando com usuário root pra liberar o firewall para a porta 9200
# -> sudo ufw allow 9200/tcp && sudo ufw reload && sudo ufw status

# mvn clean package -DskipTests

echo "Criando diretórios, se não existirem..."

mkdir -p "$APP_HOME/bin"
mkdir -p "$APP_HOME/logs"

echo "Copiando JAR para $APP_JAR..."

cp target/*.jar "$APP_JAR"
chmod 755 "$APP_JAR"

echo "Parando aplicação antiga, se existir..."

PID=$(pgrep -f "testes-0.0.1-SNAPSHOT.jar" || true)

if [ -n "$PID" ]; then
  echo "Matando processo antigo: $PID"
  kill -9 $PID
  sleep 2
else
  echo "Nenhum processo antigo encontrado."
fi

echo "Subindo aplicação..."

cd "$APP_HOME"

BUILD_ID=dontKillMe nohup java -jar "$APP_JAR" \
  --server.address=0.0.0.0 \
  --server.port="$APP_PORT" \
  > "$APP_LOG" 2>&1 &

sleep 8

echo "Verificando processo..."

NEW_PID=$(pgrep -f "testes-0.0.1-SNAPSHOT.jar" || true)

if [ -n "$NEW_PID" ]; then
  echo "Aplicação rodando com PID: $NEW_PID"
else
  echo "Aplicação não subiu."
  echo "Últimas linhas do log:"
  tail -n 100 "$APP_LOG" || true
  exit 1
fi

echo "Verificando porta..."

ss -ltnp | grep "$APP_PORT" || true

echo "Últimas linhas do log:"
tail -n 50 "$APP_LOG" || true

echo "Deploy finalizado."