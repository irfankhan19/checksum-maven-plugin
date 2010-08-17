/*
 * Copyright 2010 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nicoulaj.maven.plugins.checksum.digest;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class used to get instances of implementations of {@link Digester}.
 *
 * <p>Each {@link Digester} object is a singleton itself.</p>
 *
 * @author <a href="mailto:julien.nicoulaud@gmail.com">Julien Nicoulaud</a>
 * @see Digester
 * @since 1.0
 */
public class DigesterFactory
{
    /**
     * The instance of {@link net.nicoulaj.maven.plugins.checksum.digest.DigesterFactory}.
     */
    private static DigesterFactory instance;

    /**
     * The map (algorithm, digester).
     */
    protected Map<String, Digester> digesters = new HashMap<String, Digester>( 7 );

    /**
     * Build a new {@link net.nicoulaj.maven.plugins.checksum.digest.DigesterFactory}.
     *
     * @see #getInstance()
     */
    private DigesterFactory()
    {
    }

    /**
     * Get the instance of {@link net.nicoulaj.maven.plugins.checksum.digest.DigesterFactory}.
     *
     * @return the only instance of {@link net.nicoulaj.maven.plugins.checksum.digest.DigesterFactory}.
     */
    public static DigesterFactory getInstance()
    {
        if ( instance == null )
        {
            instance = new DigesterFactory();
        }
        return instance;
    }

    /**
     * Get an instance of {@link Digester} for the given checksum algorithm.
     *
     * @param algorithm the target checksum algorithm.
     * @return an instance of {@link Digester}.
     * @throws NoSuchAlgorithmException if the checksum algorithm is not supported or invalid.
     * @see Digester
     */
    public Digester getDigester( String algorithm ) throws NoSuchAlgorithmException
    {
        Digester digester = digesters.get( algorithm );

        if ( digester == null )
        {
            if ( "CRC32".equalsIgnoreCase( algorithm ) )
            {
                digester = new CRC32Digester();
            }
            else if ( "MD2".equalsIgnoreCase( algorithm ) )
            {
                digester = new MessageDigestDigester( "MD2" );
            }
            else if ( "MD5".equalsIgnoreCase( algorithm ) )
            {
                digester = new MessageDigestDigester( "MD5" );
            }
            else if ( "SHA-1".equalsIgnoreCase( algorithm ) )
            {
                digester = new MessageDigestDigester( "SHA-1" );
            }
            else if ( "SHA-256".equalsIgnoreCase( algorithm ) )
            {
                digester = new MessageDigestDigester( "SHA-256" );
            }
            else if ( "SHA-384".equalsIgnoreCase( algorithm ) )
            {
                digester = new MessageDigestDigester( "SHA-384" );
            }
            else if ( "SHA-512".equalsIgnoreCase( algorithm ) )
            {
                digester = new MessageDigestDigester( "SHA-512" );
            }
            else
            {
                throw new NoSuchAlgorithmException();
            }

            digesters.put( algorithm, digester );
        }

        return digester;
    }
}
