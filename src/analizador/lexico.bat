SET JAVA_HOME="C:\Program Files\Java\jdk1.8.0_211\bin"
SET PATH=%JAVA_HOME%;%PATH%
SET CLASSPATH=%JAVA_HOME%;
SET JFLEX_HOME=C:\Fuente\jflex-1.6.1
cd C:\Users\oscar\Documents\NetBeansProjects\ServidorDOSLang\src\analizador
java -jar %JFLEX_HOME%\lib\jflex-1.6.1.jar lexico.jflex
pause