# Guía para conectar un Arduino real con AforoIoT

Esta guía explica cómo dejar tu software preparado para funcionar con un Arduino físico (sensor/puerta) y cómo probar la sincronización correctamente.

---

## 1. ¿Qué necesitas?

Antes de comenzar necesitas lo siguiente:

- Una placa Arduino (por ejemplo Arduino Uno o compatible).
- Un cable USB para conectar el Arduino a tu computadora.
- El software del proyecto AforoIoT ya instalado y corriendo.
- Java 21 y Maven Wrapper (`mvnw.cmd`) disponibles en el proyecto.
- El puerto COM donde aparece tu Arduino en Windows.

---

## 2. Requisitos previos del sistema

### Windows
1. Instala el driver USB del fabricante del Arduino si tu sistema no lo reconoce.
2. Conecta el Arduino y abre el Administrador de dispositivos.
3. Busca la sección "Puertos COM y LPT".
4. Anota el número del puerto que aparece, por ejemplo:
   - COM3
   - COM4
   - COM5

> Si no ves ningún puerto COM, revisa el cable USB o instala el driver correspondiente.

---

## 3. Configuración del proyecto para usar Arduino real

En el archivo:

- `src/main/resources/application.properties`

debes tener esta configuración:

```properties
arduino.enabled=true
arduino.port=COM3
arduino.business-id=1
```

### Qué significa cada línea
- `arduino.enabled=true` → activa el listener serial del Arduino.
- `arduino.port=COM3` → indica el puerto COM donde está conectado el Arduino.
- `arduino.business-id=1` → indica a qué negocio se asociará el sensor.

### Importante
Si tu puerto no es COM3, cámbialo por el puerto real que hayas identificado en Windows.

---

## 4. Cómo saber si el Arduino está bien conectado

1. Conecta el Arduino con USB.
2. Abre el Administrador de dispositivos.
3. Confirma que aparece un puerto COM válido.
4. Repite el proceso si el puerto cambia de COM3 a COM4 o COM5.

Si el Arduino no aparece como puerto COM, no podrás usar la comunicación serial con la app.

---

## 5. Cómo arrancar la aplicación con Arduino real

Abre una terminal en la carpeta del proyecto y ejecuta:

```powershell
mvnw.cmd spring-boot:run
```

O también:

```powershell
./mvnw.cmd spring-boot:run
```

La app debería iniciar normalmente en:

```text
http://localhost:8080
```

---

## 6. Qué pasa cuando el sistema detecta el Arduino

Cuando el listener serial está activo:

- el sistema intenta abrir el puerto COM configurado;
- si el hardware está disponible, escucha señales del Arduino;
- si llega una señal `E`, registra una entrada;
- si llega una señal `S`, registra una salida.

En otras palabras:
- `E` = una persona entra.
- `S` = una persona sale.

---

## 7. Cómo probar la comunicación real

### Opción A: usar el simulador web
Si no tienes el hardware conectado todavía, puedes usar la interfaz web para probar la lógica del negocio.

### Opción B: usar Arduino real
1. Conecta tu Arduino físico.
2. Asegúrate que `arduino.enabled=true`.
3. Asegúrate que `arduino.port` coincide con tu puerto real.
4. Inicia la aplicación.
5. En la interfaz, verifica que el contador cambie cuando el Arduino envíe señales.

---

## 8. Si la app no detecta el Arduino

Si ves errores como:
- `Cannot load native library`
- `No se pudo iniciar el listener serial`
- `puerto ocupado`
- `COMX no encontrado`

entonces haz esto:

### Revisar
1. Confirma que el Arduino está conectado.
2. Confirma que el puerto COM es correcto.
3. Confirma que no está siendo usado por otra aplicación.
4. Revisa si el driver está instalado.

### Solución rápida
- Cambia `arduino.enabled=false` temporalmente si solo quieres usar la app sin hardware.
- Usa el simulador web del proyecto para pruebas.

---

## 9. Recomendación profesional para desarrollo

Para evitar que la app falle por problemas de hardware, la configuración recomendada es:

- `arduino.enabled=false` en desarrollo normal.
- `arduino.enabled=true` solo cuando vas a probar con Arduino real.

Esto mantiene la aplicación estable y evita bloqueos por librerías nativas o por puertos no disponibles.

---

## 10. Flujo recomendado de uso real

1. Conecta el Arduino a la computadora.
2. Identifica su puerto COM en Windows.
3. Edita `application.properties` con ese puerto.
4. Activa `arduino.enabled=true`.
5. Inicia la aplicación.
6. Prueba la comunicación con el hardware real.
7. Si todo funciona, deja esa configuración activa.

---

## 11. Resumen rápido

Para que el sistema funcione con Arduino real:

1. Conecta el dispositivo por USB.
2. Identifica el puerto COM correcto.
3. Ajusta `application.properties`.
4. Activa `arduino.enabled=true`.
5. Inicia la app.
6. Prueba la comunicación con el sensor.

---

## 12. Nota final

Si deseas una versión más avanzada, el siguiente paso sería conectar un sensor real de entrada/salida y programar el Arduino para enviar señales `E` y `S` al puerto serial. Ese es el punto donde la app y el hardware pueden sincronizarse completamente.
