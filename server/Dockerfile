FROM centos/python-27-centos7

COPY MtServer.class /deployments/
COPY ClientHandler.class /deployments/
COPY Player.class /deployments/
COPY Pythonreader.class /deployments/
COPY requirements.txt /deployments/
COPY twitterexample.py  /deployments/
COPY AFINN-111.txt /deployments/

USER root

RUN yum -y install java-1.8.0-openjdk
RUN yum -y install epel-release
RUN yum -y install python-devel
RUN yum -y install python-pip
RUN yum -y install python-matplotlib
RUN yum -y install python-qt4
ENV LD_LIBRARY_PATH=/opt/rh/python27/root/usr/lib64
RUN pip install --upgrade pip
RUN pip install --no-cache-dir -r /deployments/requirements.txt
RUN pip install twitter
RUN pip install oauth2
RUN pip install requests-oauth2
RUN pip install prettytable

CMD cd /deployments; java MtServer;
