@echo off
:: Thay 'ollama' bang ten container thuc te cua ban (thuong la ollama)
set CONTAINER_NAME=ollama

echo --- Dang don dep model cu trong Docker ---
docker exec %CONTAINER_NAME% ollama rm qwen-gpu-fixed

echo --- Dang tao Modelfile tam thoi tren Windows ---
echo FROM qwen2.5-coder:1.5b > Modelfile
echo PARAMETER num_gpu 50 >> Modelfile
echo PARAMETER num_ctx 4096 >> Modelfile

echo --- Copy Modelfile vao Container ---
docker cp Modelfile %CONTAINER_NAME%:/Modelfile

echo --- Dang tao model moi ben trong Docker ---
docker exec %CONTAINER_NAME% ollama create qwen-gpu-fixed -f /Modelfile

echo --- Dang don dep file tam ---
del Modelfile
docker exec %CONTAINER_NAME% rm /Modelfile

echo --- Hoan tat! Chay thu de kiem tra ---
docker exec -it %CONTAINER_NAME% ollama run qwen-gpu-fixed "Chao ban, kiem tra GPU giup toi"
pause