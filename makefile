build-all:
	make -C ./config-service build
	make -C ./discovery-service build
	make -C ./executor-service build
	make -C ./problem-service build

