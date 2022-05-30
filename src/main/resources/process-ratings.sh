#!/bin/bash

WORK_DIR='ml-25m'
LINKS_FILE="${WORK_DIR}/links.csv"
RATINGS_INPUT_FILE="${WORK_DIR}/ratings.csv"
RATINGS_OUTPUT_FILE="${WORK_DIR}/ratings-withTmdbId.csv"

function downloadMovielensDataset() {
  echo ">> Downloading the MovieLens 25M Dataset (http://files.grouplens.org/datasets/movielens/ml-25m-README.html)"
  echo
  curl -L http://files.grouplens.org/datasets/movielens/ml-25m.zip -o movielens-25m.zip
  unzip movielens-25m.zip
}

function printPercentage() {
  local count=${1:?Provide count}
  local totalCount=${2:?Provide total count}

  local percent=$(printf '%.1f\n' $(echo "$count / $totalCount * 100" | bc -l))
  echo -en "${percent}%\r"
}

function appendTmdbId() {
  # copy headers from input file to output file
  local headers=$(head -1 ${RATINGS_INPUT_FILE})
  echo "${headers},tmdbId" > ${RATINGS_OUTPUT_FILE}

  local totalCount=$(wc -l ${RATINGS_INPUT_FILE} | cut --delimiter=$' ' --fields=1)
  local count=0

  {
    read # skip CSV header

    while read line; do
      local count=$(echo "$count + 1" | bc) # count++
      printPercentage $count $totalCount

      local movieLensId=$(echo $line | cut --delimiter=, --fields=2)
      local movieDbId=$(grep --extended-regexp "^${movieLensId}," ${LINKS_FILE} | cut --delimiter=$',' --fields=3)
    
      if [ "$movieDbId" == "" ]; then
        continue
      fi

      echo "${line},${movieDbId}" >> ${RATINGS_OUTPUT_FILE}
    done

  } < ${RATINGS_INPUT_FILE}
}

downloadMovielensDataset
appendTmdbId
