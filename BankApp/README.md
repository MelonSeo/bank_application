>gradle 빌드로 실행하기
- ./gradlew run
- .\gradlew run --console=plain

> 바로 실행이 안될 시 빌드된 Jar 파일 사용
- java -jar build/libs/BankApp-1.0-SNAPSHOT-all.jar 

> Window Cmd 한글 깨짐
- chcp 65001

> SQL dump 파일로 DB 초기화
- 아까처럼 MySQL bin 폴더로 이동합니다.

cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
(경로는 본인 버전에 맞게 수정)

Import 명령어를 입력합니다.

mysql -u root -p < "C:\Users\User\Desktop\bank_db_dump.sql"


혹은  mysql -u 유저명 -p DB명 < 아웃풋.sql