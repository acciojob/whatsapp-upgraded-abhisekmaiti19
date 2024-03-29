package com.driver;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WhatsappService {

    private int groupCount=0;
    private int messageCount=0;

    HashMap<Group,List<Message>> GroupMessage=new HashMap<>();
    List<Message> myMessage=new ArrayList<>();
    HashMap<User,List<Message>> myUserList=new HashMap<>();
    HashMap<String,User> UserMap=new HashMap<>();
    HashMap<Group,List<User>> groupHashMap=new HashMap<>();


    public String  createUser(String name,String mobile)throws Exception{

        if(UserMap.containsKey(mobile))
        {
            throw new Exception("User already exists");
        }

        User user=new User(name, mobile);
        UserMap.put(mobile,user);

        return "SUCCESS";
    }

    public int createMessage(String content)
    {
        Message message=new Message(++messageCount,content);
        message.setTimestamp(new Date());
        myMessage.add(message);
        return messageCount;
    }

    public Group createGroup(List<User> users){

        if(users.size()==2)
        {
            Group group=new Group(users.get(1).getName(),2);
            groupHashMap.put(group,users);
            return group;
        }
        Group group=new Group("Group "+ ++groupCount,users.size());
        groupHashMap.put(group,users);
        return group;
    }



    public int sendMessage(Message message,User sender,Group group)throws Exception{

        if(!groupHashMap.containsKey(group))
        {
            throw new Exception("Group does not exist");
        }
        boolean senderExist=false;
        for(User user:groupHashMap.get(group))
        {
            if(user.equals(sender))
            {
                senderExist=true;   break;
            }
        }
        if(!senderExist)
        {
            throw new Exception("You are not allowed to send message");
        }

        if(GroupMessage.containsKey(group))
        {
            GroupMessage.get(group).add(message);
        }
        else
        {
            List<Message> messages=new ArrayList<>();
            messages.add(message);
            GroupMessage.put(group,messages);
        }

        if(myUserList.containsKey(sender))
        {
            myUserList.get(sender).add(message);
        }
        else
        {
            List<Message> messages=new ArrayList<>();
            messages.add(message);
            myUserList.put(sender,messages);
        }

        return GroupMessage.get(group).size();
    }


    public String changeAdmin(User approver, User user, Group group)throws Exception{

        if(!groupHashMap.containsKey(group))
        {
            throw new Exception("Group does not exist");
        }

        User pastAdmin=groupHashMap.get(group).get(0);

        if(!approver.equals(pastAdmin))
        {
            throw new Exception("Approver does not have rights");
        }

        boolean check=false;
        for(User user1:groupHashMap.get(group))
        {
            if(user1.equals(user))   check=true;
        }

        if(!check)
        {
            throw new Exception("User is not a participant");
        }

        User newAdmin=null;

        Iterator<User> userIterator = groupHashMap.get(group).iterator();

        while(userIterator.hasNext())
        {
            User u= userIterator.next();
            if(u.equals(user))
            {
                newAdmin = u;
                userIterator.remove();
            }
        }

        groupHashMap.get(group).add(0,newAdmin);
        return  "SUCCESS";

    }
    public int removeUser(User user)throws Exception {

        boolean userFound = false;
        int groupSize = 0;
        int messageCount = 0;
        int overallMessageCount = myMessage.size();
        Group groupToRemoveFrom = null;
        for (Map.Entry<Group, List<User>> entry : groupHashMap.entrySet()) {
            List<User> groupUsers = entry.getValue();
            if (groupUsers.contains(user))
            {
                userFound = true;
                groupToRemoveFrom = entry.getKey();
                if (groupUsers.get(0).equals(user))
                {
                    throw new Exception("Cannot remove admin");
                }
                groupUsers.remove(user);
                groupSize = groupUsers.size();
                break;
            }
        }
        if (!userFound)
        {
            throw new Exception("User not found");
        }

        if (myUserList.containsKey(user))
        {
            messageCount = myUserList.get(user).size() - 2;
            myUserList.remove(user);
        }


        return groupSize + messageCount + overallMessageCount;

    }
    public String findMessage(Date start, Date end, int k) {

        return "Wait";
    }
}