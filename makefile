SERVICES = config-service discovery-service gateway-service problem-service submission-service executor-service

build:
	@for svc in $(SERVICES); do \
		make -C ./$$svc build; \
	done