Proyecto Semestral POO

Grupo: Los POOgramadores
Integrantes: Osvaldo Baeza, Vicente Navarrete.

Requisitos:
JDK 21
IntelliJ IDEA

Instrucciones:
Descargar el repositorio y abrirlo en IntelliJ.
El codigo se ejecuta desde la carpeta \src\vista\Main.
Ejecutar main con el boton verde de el extremo superior derecho.

Carpeta Datos:
La carpeta datos debe estar en el directorio desde donde se ejecuta (src).
meteorologia.dat es un archivo creado con serialización de objetos que contiene la lista de regiones y a partir de ella, el grafo completo de objetos: comunas, estaciones, sensores, mediciones y alertas, con sus asociaciones.
La opcion Guardar datos sobrescribe el archivo.
No se debe editar a mano.

Tipos de sensor: TEMPERATURA, HUMEDAD, PRESION, VIENTO, PRECIPITACION.
Estados: ACTIVO, INACTIVO.
Las alertas no se guardan: se generan de nuevo al cargar las mediciones.
