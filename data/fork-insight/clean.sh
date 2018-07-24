#!/bin/bash 
`cat $1 | sed -E 's/^\"(.*)\"$/\{\1\};/' | sed -E 's/, /;/g' | sed -E 's/^([^\{]*)$/{\1};/' | sed -E 's/[^a-zA-Z0-9 ;{}]//g' | iconv -f UTF-8 -t ASCII//TRANSLIT > $2`
