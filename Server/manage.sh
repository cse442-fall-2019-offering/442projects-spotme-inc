#!/bin/sh
FLASK_APP=. FLASK_ENV=development pipenv run flask "$@"
