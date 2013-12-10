package com.rapidbackend.socialutil.model.util;

import com.rapidbackend.socialutil.model.*;

/**
 * Helper class to find model class type by String
 * TODO use class loader to load the model classes, not in such stupid manner
 * @author chiqiu
 *@genereated by DaoGenerator
 */
public class ModelTypeFinder extends TypeFinder {

    protected Class<?>[] knownModelClasses = {
                com.rapidbackend.socialutil.model.City.class,
        com.rapidbackend.socialutil.model.Feedsource.class,
        com.rapidbackend.socialutil.model.Fileuploaded.class,
        com.rapidbackend.socialutil.model.Group.class,
        com.rapidbackend.socialutil.model.Groupfeed.class,
        com.rapidbackend.socialutil.model.Groupfeedcomment.class,
        com.rapidbackend.socialutil.model.Groupfeedcontent.class,
        com.rapidbackend.socialutil.model.Groupsubscription.class,
        com.rapidbackend.socialutil.model.Oauthapplication.class,
        com.rapidbackend.socialutil.model.Oauthapplicationuser.class,
        com.rapidbackend.socialutil.model.Oauthtokenassociation.class,
        com.rapidbackend.socialutil.model.Profiles.class,
        com.rapidbackend.socialutil.model.User.class,
        com.rapidbackend.socialutil.model.Userfeed.class,
        com.rapidbackend.socialutil.model.Userfeedcomment.class,
        com.rapidbackend.socialutil.model.Userfeedcontent.class,
        com.rapidbackend.socialutil.model.Usersubscription.class,
    };
    
    public Class<?>[] getKnownModelClasses() {
        return knownModelClasses;
    }

    public void setKnownModelClasses(Class<?>[] knownModelClasses) {
        this.knownModelClasses = knownModelClasses;
    }
    
    /**
     * Find class by className string.
     * Override this method if you have different rules.
     * It is now called in handlers and request schema.
     * We can reconfig it in spring xmls.
     * @param className full class name of this class
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Class getModelClass(String className){
        Class clazz = null;
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.City")) {
            clazz = com.rapidbackend.socialutil.model.City.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Feedsource")) {
            clazz = com.rapidbackend.socialutil.model.Feedsource.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Fileuploaded")) {
            clazz = com.rapidbackend.socialutil.model.Fileuploaded.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Group")) {
            clazz = com.rapidbackend.socialutil.model.Group.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Groupfeed")) {
            clazz = com.rapidbackend.socialutil.model.Groupfeed.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Groupfeedcomment")) {
            clazz = com.rapidbackend.socialutil.model.Groupfeedcomment.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Groupfeedcontent")) {
            clazz = com.rapidbackend.socialutil.model.Groupfeedcontent.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Groupsubscription")) {
            clazz = com.rapidbackend.socialutil.model.Groupsubscription.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Oauthapplication")) {
            clazz = com.rapidbackend.socialutil.model.Oauthapplication.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Oauthapplicationuser")) {
            clazz = com.rapidbackend.socialutil.model.Oauthapplicationuser.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Oauthtokenassociation")) {
            clazz = com.rapidbackend.socialutil.model.Oauthtokenassociation.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Profiles")) {
            clazz = com.rapidbackend.socialutil.model.Profiles.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.User")) {
            clazz = com.rapidbackend.socialutil.model.User.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Userfeed")) {
            clazz = com.rapidbackend.socialutil.model.Userfeed.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Userfeedcomment")) {
            clazz = com.rapidbackend.socialutil.model.Userfeedcomment.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Userfeedcontent")) {
            clazz = com.rapidbackend.socialutil.model.Userfeedcontent.class;
        }
        if (className.equalsIgnoreCase("com.rapidbackend.socialutil.model.Usersubscription")) {
            clazz = com.rapidbackend.socialutil.model.Usersubscription.class;
        }
        if (className.equalsIgnoreCase("City")) {
            clazz = com.rapidbackend.socialutil.model.City.class;
        }
        if (className.equalsIgnoreCase("Feedsource")) {
            clazz = com.rapidbackend.socialutil.model.Feedsource.class;
        }
        if (className.equalsIgnoreCase("Fileuploaded")) {
            clazz = com.rapidbackend.socialutil.model.Fileuploaded.class;
        }
        if (className.equalsIgnoreCase("Group")) {
            clazz = com.rapidbackend.socialutil.model.Group.class;
        }
        if (className.equalsIgnoreCase("Groupfeed")) {
            clazz = com.rapidbackend.socialutil.model.Groupfeed.class;
        }
        if (className.equalsIgnoreCase("Groupfeedcomment")) {
            clazz = com.rapidbackend.socialutil.model.Groupfeedcomment.class;
        }
        if (className.equalsIgnoreCase("Groupfeedcontent")) {
            clazz = com.rapidbackend.socialutil.model.Groupfeedcontent.class;
        }
        if (className.equalsIgnoreCase("Groupsubscription")) {
            clazz = com.rapidbackend.socialutil.model.Groupsubscription.class;
        }
        if (className.equalsIgnoreCase("Oauthapplication")) {
            clazz = com.rapidbackend.socialutil.model.Oauthapplication.class;
        }
        if (className.equalsIgnoreCase("Oauthapplicationuser")) {
            clazz = com.rapidbackend.socialutil.model.Oauthapplicationuser.class;
        }
        if (className.equalsIgnoreCase("Oauthtokenassociation")) {
            clazz = com.rapidbackend.socialutil.model.Oauthtokenassociation.class;
        }
        if (className.equalsIgnoreCase("Profiles")) {
            clazz = com.rapidbackend.socialutil.model.Profiles.class;
        }
        if (className.equalsIgnoreCase("User")) {
            clazz = com.rapidbackend.socialutil.model.User.class;
        }
        if (className.equalsIgnoreCase("Userfeed")) {
            clazz = com.rapidbackend.socialutil.model.Userfeed.class;
        }
        if (className.equalsIgnoreCase("Userfeedcomment")) {
            clazz = com.rapidbackend.socialutil.model.Userfeedcomment.class;
        }
        if (className.equalsIgnoreCase("Userfeedcontent")) {
            clazz = com.rapidbackend.socialutil.model.Userfeedcontent.class;
        }
        if (className.equalsIgnoreCase("Usersubscription")) {
            clazz = com.rapidbackend.socialutil.model.Usersubscription.class;
        }
        if(null == clazz)
            throw new UnsupportedOperationException("unsupported model class, name:"+className);
        return clazz;
    }
}