import hudson.model.*
import jenkins.model.*
import hudson.slaves.*
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry
import com.cloudbees.jenkins.plugins.sshslaves.verification.*
import com.cloudbees.jenkins.plugins.sshslaves.SSHConnectionDetails
import com.cloudbees.hudson.plugins.folder.*
import com.cloudbees.jenkins.plugins.foldersplus.*

def addAgent(String agentName, String description, String labels, String hostName, String credId, Integer portNumber) {
  agentPath = "/var/lib/jenkins"
  
  // Pick one of the strategies from the comments below this line
  // = new TrustInitialConnectionVerificationStrategy(false)
  // = new TrustInitialConnectionVerificationStrategy(false /* "Require manual verification of initial connection" */) // "Manually trusted key verification Strategy"
  // = new ManuallyConnectionVerificationStrategy("<your-key-here>") // "Manually provided key verification Strategy"
  // = new KnownHostsConnectionVerificationStrategy() // "~/.ssh/known_hosts file Verification Strategy"
  ServerKeyVerificationStrategy serverKeyVerificationStrategy = new BlindTrustConnectionVerificationStrategy() // "Non-verifying Verification Strategy"

  // Define a "Launch method": "Launch agents via SSH"
  ComputerLauncher launcher = new com.cloudbees.jenkins.plugins.sshslaves.SSHLauncher(
          hostName, // Host
          new SSHConnectionDetails(
                  credId, // Credentials ID
                  portNumber, // port
                  (String)null, // JavaPath
                  (String)null, // JVM Options
                  (String)null, // Prefix Start Agent Command
                  (String)null, // Suffix Start Agent Command
                  (boolean)false, // Log environment on initial connect
                  (ServerKeyVerificationStrategy) serverKeyVerificationStrategy // Host Key Verification Strategy
          )
  )

  // Define a "Permanent Agent"
  Slave agent = new DumbSlave(
          agentName,
          agentPath,
          launcher)
  agent.nodeDescription = description
  agent.numExecutors = 2
  agent.labelString = labels
  agent.mode = Node.Mode.NORMAL
  agent.retentionStrategy = new RetentionStrategy.Always()

  // Env vars; uncomment if you're using it
  /*
  List<Entry> env = new ArrayList<Entry>();
  env.add(new Entry("key1","value1"))
  env.add(new Entry("key2","value2"))
  EnvironmentVariablesNodeProperty envPro = new EnvironmentVariablesNodeProperty(env);

  agent.getNodeProperties().add(envPro)
  */

  // Add the agent
  Jenkins.instance.addNode(agent)

  print "Agent " + agentName + " has been added successfully."
  return agentName
}

def addAgentToFolder(String nodeName) {
  String folderName = currentBuild.rawBuild.getParent().getParent().getFullName();
  print "Working from folder " + folderName + "..."
  Slave node = Jenkins.instance.getNode(nodeName)
  node.getNodeProperties().add(new SecurityTokensNodeProperty(false));
  SecurityToken token = SecurityToken.newInstance();
  node.getNodeProperties().get(SecurityTokensNodeProperty.class).addSecurityToken(token);
  node.save()
  SecurityGrant request = SecurityGrant.newInstance();
  SecurityGrant grant = token.issue(request);

  folder = Jenkins.instance.getItemByFullName(folderName)
  // if (jenkins.model.Jenkins.instance.getItemByFullName(folderName) == null) {
  //   folder = Jenkins.instance.createProject(Folder.class, folderName)
  // }

  folder.getProperties().replace(new SecurityGrantsFolderProperty(Collections.<SecurityGrant>emptyList()));
  folder.getProperties().get(SecurityGrantsFolderProperty.class).addSecurityGrant(grant);
  folder.save()
  print "Agent " + nodeName + " has been added to " + folderName + " successfully."
  return true
}

def removeAgent(String nodeName) {
  String folderName = currentBuild.rawBuild.getParent().getParent().getFullName();
  print "Working from folder " + folderName + "..."
  
  folder = Jenkins.instance.getItemByFullName(folderName)
  folder.getProperties().replace(new SecurityGrantsFolderProperty(Collections.<SecurityGrant>emptyList()));
  folder.save()
  
  Slave node = Jenkins.instance.getNode(nodeName)
  Jenkins.instance.removeNode(nodeName)
}