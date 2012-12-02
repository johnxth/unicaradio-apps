#!/bin/bash

RESOLUTION_NAMES=("mdpi" "hdpi" "xhdpi")
RESOLUTIONS=(32 48 72)

for R in ${RESOLUTION_NAMES[@]}
do
	mkdir -pv png/${R}
done

for FILE in $(ls *.svg)
do
	i=0
	for R in ${RESOLUTIONS[@]}
	do
		filename=$(echo "png/${RESOLUTION_NAMES[i]}/${FILE}" | sed "s/\.svg/\.png/")
		inkscape -z -f ${FILE} -e ${filename} -C -w ${R} -h ${R} -b "#000000" -y 0.0 2&> /dev/null
		#optipng -quiet -o7 -out ${filename} ${filename}

		((i++))
	done
done
