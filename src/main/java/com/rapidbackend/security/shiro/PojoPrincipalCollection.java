package com.rapidbackend.security.shiro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.subject.MutablePrincipalCollection;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * pojo implementation of PrincipalCollection. make all principals easy to be
 * serialized and deserialized to json. All principals will be written as String.
 * Note: this is not a thread-safe implemetation.
 * @author chiqiu
 *
 */
public class PojoPrincipalCollection implements MutablePrincipalCollection{
    
    /**
     * 
     */
    private static final long serialVersionUID = -4067621647143134072L;
    protected List<RealmPrincipal> realmPrincipals;
    
    public List<RealmPrincipal> getRealmPrincipals() {
        return realmPrincipals;
    }

    public void setRealmPrincipals(List<RealmPrincipal> realmPrincipals) {
        this.realmPrincipals = realmPrincipals;
    }
    /**
     * used for deserializing
     */
    public PojoPrincipalCollection(){}
    
    public PojoPrincipalCollection(Object principal, String realmName){
        if (principal instanceof Collection) {
            addAll((Collection) principal, realmName);
        } else {
            add(principal, realmName);
        }
    }
    
    public PojoPrincipalCollection(Collection principals, String realmName){
        addAll(principals, realmName);
    }
    /**
     * Adds the given principal to this collection.
     *
     * @param principal the principal to be added.
     * @param realmName the realm this principal came from.
     */
    @Override
    public void add(Object principal, String realmName){
        if (StringUtils.isEmpty(realmName)) {
            throw new IllegalArgumentException("realmName argument cannot be null.");
        }
        if (principal == null) {
            throw new IllegalArgumentException("principal argument cannot be null.");
        }
        this.cachedToString = null;
        if(realmPrincipals!=null){// We will not have too many principals for now, so for-for is ok for now
            // remember to change this implementation if we have more than ten realms for one user.
            for (RealmPrincipal realmPrincipal: realmPrincipals) {
                if(realmPrincipal.getRealmName()!=null && 
                        realmPrincipal.getRealmName().equals(realmName) ){
                    realmPrincipal.add(principal);
                }
            }
        }else {
            realmPrincipals = new ArrayList<PojoPrincipalCollection.RealmPrincipal>();
            RealmPrincipal realmPrincipal = new RealmPrincipal();
            realmPrincipal.add(principal);
            realmPrincipals.add(realmPrincipal);
            setEmpty(false);
        }
    }

    /**
     * Adds all of the principals in the given collection to this collection.
     *
     * @param principals the principals to be added.
     * @param realmName  the realm these principals came from.
     */
    @Override
    public void addAll(Collection principals, String realmName){
        if (realmName == null) {
            throw new IllegalArgumentException("realmName argument cannot be null.");
        }
        if (principals == null) {
            throw new IllegalArgumentException("principals argument cannot be null.");
        }
        if (principals.isEmpty()) {
            throw new IllegalArgumentException("principals argument cannot be an empty collection.");
        }
        this.cachedToString = null;
        for(Object principal : principals){
            add(principal, realmName);
        }
    }

    /**
     * Adds all of the principals from the given principal collection to this collection.
     *
     * @param principals the principals to add.
     */
    @Override
    public void addAll(PrincipalCollection principals){
        if (principals.getRealmNames() != null) {
            for (String realmName : principals.getRealmNames()) {
                for (Object principal : principals.fromRealm(realmName)) {
                    add(principal, realmName);
                }
            }
        }
    }

    /**
     * Removes all Principals in this collection.
     */
    @Override
    public void clear(){
        realmPrincipals = null;
        setEmpty(true);
    }
    
    /**
     * Returns the primary principal used application-wide to uniquely identify the owning account/Subject.
     * <p/>
     * The value is usually always a uniquely identifying attribute specific to the data source that retrieved the
     * account data.  Some examples:
     * <ul>
     * <li>a {@link java.util.UUID UUID}</li>
     * <li>a {@code long} value such as a surrogate primary key in a relational database</li>
     * <li>an LDAP UUID or static DN</li>
     * <li>a String username unique across all user accounts</li>
     * </ul>
     * <h3>Multi-Realm Applications</h3>
     * In a single-{@code Realm} application, typically there is only ever one unique principal to retain and that
     * is the value returned from this method.  However, in a multi-{@code Realm} application, where the
     * {@code PrincipalCollection} might retain principals across more than one realm, the value returned from this
     * method should be the single principal that uniquely identifies the subject for the entire application.
     * <p/>
     * That value is of course application specific, but most applications will typically choose one of the primary
     * principals from one of the {@code Realm}s.
     * <p/>
     * Shiro's default implementations of this interface make this
     * assumption by usually simply returning {@link #iterator()}.{@link java.util.Iterator#next() next()}, which just
     * returns the first returned principal obtained from the first consulted/configured {@code Realm} during the
     * authentication attempt.  This means in a multi-{@code Realm} application, {@code Realm} configuraiton order
     * matters if you want to retain this default heuristic.
     * <p/>
     * If this heuristic is not sufficient, most Shiro end-users will need to implement a custom
     * {@link org.apache.shiro.authc.pam.AuthenticationStrategy}.  An {@code AuthenticationStrategy} has exact control
     * over the {@link PrincipalCollection} returned at the end of an authentication attempt via the
     * <code>AuthenticationStrategy#{@link org.apache.shiro.authc.pam.AuthenticationStrategy#afterAllAttempts(org.apache.shiro.authc.AuthenticationToken, org.apache.shiro.authc.AuthenticationInfo) afterAllAttempts}</code>
     * implementation.
     *
     * @return the primary principal used to uniquely identify the owning account/Subject
     * @since 1.0
     */
    @Override
    public Object getPrimaryPrincipal(){
        if(isEmpty()){
            return null;
        }else {
            return getRealmPrincipals().get(0).getPrincipals().get(0);
        }
    }
    protected Object primaryPrincipal;
    
    public void setPrimaryPrincipal(Object primaryPrincipal) {
        this.primaryPrincipal = primaryPrincipal;
    }

    /**
     * Returns the first discovered principal assignable from the specified type, or {@code null} if there are none
     * of the specified type.
     * <p/>
     * Note that this will return {@code null} if the 'owning' subject has not yet logged in.
     *
     * @param type the type of the principal that should be returned.
     * @return a principal of the specified type or {@code null} if there isn't one of the specified type.
     */
    public <T> T oneByType(Class<T> type){
        throw new RuntimeException("PojoPrincipalCollection.oneByType(Class<T> type) should never be called");
    }

    /**
     * Returns all principals assignable from the specified type, or an empty Collection if no principals of that
     * type are contained.
     * <p/>
     * Note that this will return an empty Collection if the 'owning' subject has not yet logged in.
     *
     * @param type the type of the principals that should be returned.
     * @return a Collection of principals that are assignable from the specified type, or
     *         an empty Collection if no principals of this type are associated.
     */
    @Override
    public <T> Collection<T> byType(Class<T> type){
        throw new RuntimeException("PojoPrincipalCollection.byType(Class<T> type) should never be called");
    }

    /**
     * Returns a single Subject's principals retrieved from all configured Realms as a List, or an empty List if
     * there are not any principals.
     * <p/>
     * Note that this will return an empty List if the 'owning' subject has not yet logged in.
     *
     * @return a single Subject's principals retrieved from all configured Realms as a List.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List asList(){
        Set all = asSet();
        if (all.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(new ArrayList(all));
    }

    /**
     * Returns a single Subject's principals retrieved from all configured Realms as a Set, or an empty Set if there
     * are not any principals.
     * <p/>
     * Note that this will return an empty Set if the 'owning' subject has not yet logged in.
     *
     * @return a single Subject's principals retrieved from all configured Realms as a Set.
     */
    public Set asSet(){
        Set<Object> set = new HashSet<Object>();
        if(!isEmpty()){
            for(RealmPrincipal realmPrincipal:realmPrincipals){
                for(Object s: realmPrincipal.getPrincipals()){
                    set.add(s);
                }
            }
        }
        return set;
    }

    /**
     * Returns a single Subject's principals retrieved from the specified Realm <em>only</em> as a Collection, or an empty
     * Collection if there are not any principals from that realm.
     * <p/>
     * Note that this will return an empty Collection if the 'owning' subject has not yet logged in.
     *
     * @param realmName the name of the Realm from which the principals were retrieved.
     * @return the Subject's principals from the specified Realm only as a Collection or an empty Collection if there
     *         are not any principals from that realm.
     */
    public Collection fromRealm(String realmName){
        if(getRealmPrincipals().size()<=0 || StringUtils.isEmpty(realmName)){
            return Collections.EMPTY_LIST;
        }else {
            List<Object> result = null;
            for(RealmPrincipal realmPrincipal : getRealmPrincipals()){
                if(realmPrincipal.getRealmName()!=null&& realmPrincipal.getRealmName().equals(realmName)){
                    result = realmPrincipal.getPrincipals();
                }
            }
            return result;
        }
    }

    /**
     * Returns the realm names that this collection has principals for.
     *
     * @return the names of realms that this collection has one or more principals for.
     */
    public Set<String> getRealmNames(){
        if(getRealmPrincipals().size()<=0){
            return Collections.EMPTY_SET;
        }else {
            Set<String> result = new HashSet<String>();
            for (RealmPrincipal realmPrincipal : getRealmPrincipals()) {
                result.add(realmPrincipal.getRealmName());
            }
            return result;
        }
    }

    /**
     * Returns {@code true} if this collection is empty, {@code false} otherwise.
     *
     * @return {@code true} if this collection is empty, {@code false} otherwise.
     */
    boolean empty = true;
    
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
    @Override
    public boolean isEmpty() {
        return empty;
    }

    public static class RealmPrincipal{
        protected String realmName;
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
        protected List<Object> principals;
        
        public String getRealmName() {
            return realmName;
        }
        public void setRealmName(String realmName) {
            this.realmName = realmName;
        }
        public List<Object> getPrincipals() {
            return principals;
        }
        public void setPrincipals(List<Object> principals) {
            this.principals = principals;
        }
        public void add(Object principal){
            if(getPrincipals()!=null){
                if(!getPrincipals().contains(principal)){
                    getPrincipals().add(principal);
                }
            }else {
                setPrincipals(new ArrayList<Object>());
                getPrincipals().add(principal);
            }
        }
    }
    @SuppressWarnings("rawtypes")
    @Override
    public Iterator iterator() {
        return asSet().iterator();
    }
    
    private transient String cachedToString;
    
    public String toString() {
        if (this.cachedToString == null) {
            Set<Object> principals = asSet();
            if (!CollectionUtils.isEmpty(principals)) {
                this.cachedToString = StringUtils.join(principals.toArray(), ',');
            } else {
                this.cachedToString = "empty";
            }
        }
        return this.cachedToString;
    }
}
