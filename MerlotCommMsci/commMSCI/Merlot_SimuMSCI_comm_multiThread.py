######################################################
##  Author: HDB
##
##  v1: Inject data MSCI to a query systems
##
######################################################

import random
import sys
import requests
import threading
import logging

import ConfigParser

global param

class  Parametre:
    config = ConfigParser.RawConfigParser()
    config.read('propertieMSCI.ini')
    serverUrl = config.get('SERVER', 'serverUrl')
    userName = config.get('SERVER', 'userName')
    password = config.get('SERVER', 'password')
    nameSystem = config.get('SYSTEM', 'nameSystem')
    accountName = config.get('SYSTEM', 'accountName')
    site = config.get('SYSTEM','site')
    nameAdmin  = config.get('SERVER', 'nameAdmin')
    passwordAdmin  = config.get('SERVER', 'passwordAdmin')

class SystemMSCI:

    def __init__(self):
        self.Latitude = 0
        self.Longitude=0
        self.RSSI=0
        self.ECIO=0
        self.BytesSent =0
        self.BytesReceived  =0
        self.ServiceType   =""
        self.Operator     =""
        self.localisation =""
        self.avms=0

    def genereDonnees(self, site):
        lat = 0
        lon = 0

        serviceTypeWords = ['EDGE', 'HSPA', '3G+','UMTS','GPRS', 'HSDPA', 'HSPA+', 'EVDO/1x', 'EVDO']
        operatorWords = ['BYTEL,20820', 'SFR', 'ORANGE F,20801','F BOUYGUES TELECOM, 20820']
        #coordonnees GPS
        a = int( _randomDouble(-100000,100000))
        b = int( _randomDouble(-100000,100000))
        siteT = site.split(',')
        nbSite =  len(siteT)
        _site =  siteT[int(_randomInt(0,nbSite))]
        self.localisation =_site
        #print (_site)

        if _site == "FRANCE":
           lat =  4353603
           lon =  151368
        if _site == "USA":  #35.01856,-96.94350600000001
           lat =  3501856
           lon =  -9694350
        if _site == "ASIE": #Pekin : 39.90403,116.40752599999996
           lat =  3990403
           lon=   11640752
        if _site == "AUSTRALIE": #Sydney : -33.8674869,151.20699020000006
           lat = -3386748
           lon = 15120699
           
        self.Latitude = lat + a
        self.Longitude= lon + b
        #
        self.RSSI = _randomDouble(-30,-110)
        self.ECIO = _randomDouble(-1,-20)
        self.BytesSent = _randomInt (1000,2000)
        self.BytesReceived = _randomInt (500,900)

        self.ServiceType =  serviceTypeWords[int(_randomInt(1,9))]
        self.Operator =   operatorWords[int(_randomInt(1,4))]
        
        self.avms = _randomInt (0,2)

    def afficheDonnees(self):
        print(self.RSSI,self.ECIO,self.BytesSent,self.BytesReceived,self.Latitude, self.Longitude,self.ServiceType,self.Operator, self.avms, "===>" , self.localisation)



## Generate a random int in the range passed as parameter and convert it to string
def _randomInt(startRange, endRange):
        prefix = random.randrange(startRange, endRange)
        result = str(prefix)
        return (result)

## Generate random realistics values for the different data
def _randomDouble(startRange, endRange):
        prefix = random.uniform(startRange, endRange)
        prefix = '%.0f' % round(prefix, 0)
        result = str(prefix)
        return (result)


def __init__():
    global param
    param = Parametre()

def _getAccountUid():
    global param
    api_url = param.serverUrl + "/backoffice/"
    request_url = api_url + "/companies?name=" + param.accountName
    req = requests.get(request_url, auth=(param.nameAdmin, param.passwordAdmin))
    response = req.json()
    accountUid = response["items"][0]["uid"]
    return (accountUid)


def _getToken():
    global param
    api_url = param.serverUrl + "/api/oauth/token"
    logging.info(api_url)
    username = param.userName
    password  =param.password
    #accountName = param.accountName
    request_url = api_url + "?client_id=085d82466f824079a829f301b8a1f492&grant_type=password&username="+ username + "&password=" + password +"&client_secret=6854fa72f98e492db163458085d5b89c"
    req = requests.get(request_url)
    response = req.json()
    getToken = response["access_token"]
    return (getToken)

def _getSystemList():
    global param
    api_url = param.serverUrl + "/api/v1"
    token = _getToken()
    accountuid =_getAccountUid()
    if len(param.nameSystem)>0:
        request_url = api_url + "/systems?company="+ accountuid +"&name=" + param.nameSystem + "&states=DEPLOYED,READY&access_token=" + token
    else:
        request_url = api_url + "/systems?company="+ accountuid + "&states=DEPLOYED,READY&access_token=" + token
    req = requests.get(request_url)
    response = req.json()
    return (response)


def createRequest(serialNumber,site):
    sn=serialNumber
    #print (sn)
    data = SystemMSCI()
    data.genereDonnees(site)
    #data.afficheDonnees()

    postRequestContent = ("\n" +
    "<body> \n "    +
    "<readResponse> \n"    +
    "<hdr id=\"25\">" + sn + "</hdr>  \n " +
    "<sts id=\"261\">"+  str(data.RSSI) + "</sts>  \n " +
    "<sts id=\"902\">" + str(data.Latitude) + "</sts> "
    "<sts id=\"903\">" + str(data.Longitude) + "</sts>   \n " +
    "<sts id=\"264\">" + str(data.ServiceType) +"</sts>   \n " +
    "<sts id=\"283\">" + str(data.BytesSent) + "</sts>  \n " +
    "<sts id=\"284\">" + str(data.BytesReceived) + "</sts>  \n " +
    "<sts id=\"772\">" + str(data.ECIO) + "</sts>  \n " +
    "<sts id=\"770\">" + str(data.Operator) + "</sts>  \n " +
    "<sts id=\"773\">Cell ID </sts>  \n " +
    "<sts id=\"5026\">" + str(data.avms) + "</sts>  \n " +
    "</readResponse></body>"
    )
    #print(postRequestContent)
    return(postRequestContent)

def postRequest(url, sn,site ):
    createrequest = createRequest(str(sn),site)
    req = requests.post(url, data=createrequest)
    #print(req.text)

def launchComm(threadname, postUrl,systemlist, i1, i2):
    
    while (i1 <= i2)  :
       
        sn = systemlist["items"][i1]["gateway"]["serialNumber"]
        logging.debug(threadname +  "   i = " + str(i1) + "===" + sn)
        if sn is not None :
            postRequest(postUrl, sn, param.site)
        i1 = i1 + 1 
    


class myThread (threading.Thread):
    def __init__(self, threadID, postUrl, systemList, i1, i2):
        threading.Thread.__init__(self)
        self._stopevent = threading.Event( )
        self.threadID = threadID
        self.postUrl = postUrl
        self.systemList = systemList
        self.i1 = i1
        self.i2 = i2
        
        
    def run(self):             
        launchComm (self.name, self.postUrl, self.systemList, self.i1, self.i2)
        self._stopevent.wait(2.0)
        logging.debug( "le thread "+self.name +" s'est termine proprement")

    
    def stop(self):
        self.Terminated = True    
        
def main(argv):
    global param
    __init__()
    #global param
    
    logger = logging.getLogger("MSCI Comm.")
    logging.basicConfig(level=logging.DEBUG)  
    console = logging.StreamHandler()
    #console.setLevel(logging.INFO)
    #formatter = logging.Formatter('%(levelname)s -%(asctime)s - %(name)s -  %(message)s')
    #console.setFormatter(formatter)
    
    logger.addHandler(console)
    
    postUrl = param.serverUrl + "/device/msci"
    logger.debug ("Retieve systems list ...wait")
    systemlist=_getSystemList()
    count = int(systemlist["count"])
    logger.debug ( "Nombre de system= " + str(count) + " Systemes")
    nbreThread = 10;
    i = 1
    start = 0
    
    #return
    
    while (i <= nbreThread) :
        end = start + (count/nbreThread)-1
        logger.debug( "i = " +  str(i)  + "  start end =" +  str(start) + " "  + str(end))
        thread =  myThread(i, postUrl, systemlist, start, end )
        thread.start()
        start = end + 1 
        i = i + 1
    
      
if __name__ == "__main__":
    sys.exit(main(sys.argv))