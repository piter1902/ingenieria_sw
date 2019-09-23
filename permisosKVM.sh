#!/bin/bash

# Permisos para ejecutar el emulador de android studio
# Hay que dar permisos de ejecucion al 'fichero'/'dispositivo' /dev/kvm

target='/dev/kvm'
mask='0777'

if [ $(id -u) -ne 0 ]; then
	# Necesitas permisos de administrador
	echo 'Necesitas permisos de administrador (ejecutar con sudo)' &>2
	exit 1
fi

chmod $mask $target
chown $LOGNAME $target
if [ $? -eq 0 ]; then
	# Todo ha ido bien
	echo 'Todo ha ido bien'
	echo 'Mucha suerte programando :D'
	exit 0
else
	echo 'Ha habido algun problema, contacte con su administrador favorito.' &>2
	exit 2
fi
