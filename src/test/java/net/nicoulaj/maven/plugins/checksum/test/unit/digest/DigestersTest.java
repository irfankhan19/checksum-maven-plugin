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
package net.nicoulaj.maven.plugins.checksum.test.unit.digest;

import net.nicoulaj.maven.plugins.checksum.digest.Digester;
import net.nicoulaj.maven.plugins.checksum.digest.DigesterException;
import net.nicoulaj.maven.plugins.checksum.digest.DigesterFactory;
import net.nicoulaj.maven.plugins.checksum.test.unit.Utils;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Tests for each implementation of {@link Digester}.
 *
 * @author <a href="mailto:julien.nicoulaud@gmail.com">Julien Nicoulaud</a>
 * @see Digester
 * @since 0.1
 */
@RunWith( Parameterized.class )
public class DigestersTest
{
    /**
     * The {@link Digester} tested.
     */
    private Digester digester;

    /**
     * Rule used to specify per-test expected exceptions.
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Generate the list of arguments with which the test should be run.
     *
     * @return the list of tested {@link Digester} implementations.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> getTestParameters()
    {
        Object[][] data = new Object[][]{{"CRC32"}, {"MD2"}, {"MD5"}, {"SHA-1"}, {"SHA-256"}, {"SHA-384"}, {"SHA-512"}};
        return Arrays.asList( data );
    }

    /**
     * Build a new {@link DigestersTest}.
     *
     * @param algorithm the target checksum algorithm to run the test for.
     */
    public DigestersTest( String algorithm ) throws NoSuchAlgorithmException
    {
        this.digester = DigesterFactory.getInstance().getDigester( algorithm );
    }

    /**
     * Assert the algorithm name is not null/empty.
     */
    @Test
    public void testAlgorithmNameDefined()
    {
        String algorithmName = digester.getAlgorithm();
        Assert.assertNotNull( "The algorithm name is null.", algorithmName );
        Assert.assertTrue( "The algorithm name is empty.", algorithmName.length() > 0 );
    }

    /**
     * Assert the file name extension is not null/empty.
     */
    @Test
    public void testFilenameExtensionDefined()
    {
        String filenameExtension = digester.getFilenameExtension();
        Assert.assertNotNull( "The file name extension is null.", filenameExtension );
        Assert.assertTrue( "The file name extension is empty.", filenameExtension.length() > 0 );
    }

    /**
     * Check the calculated checksum for a specific file with the {@link Digester#calc(File)} method is valid against a
     * pre-calculated checksum.
     *
     * @throws DigesterException if there was a problem while calculating the checksum.
     * @throws IOException       if there was a problem reading the file containing the pre-calculated checksum.
     * @see Digester#calc(java.io.File)
     */
    @Test
    public void testCalc() throws DigesterException, IOException
    {
        List<File> testFiles = FileUtils.getFiles( new File( Utils.SAMPLE_FILES_PATH ), null, null );
        for ( File testFile : testFiles )
        {
            String calculatedHash = digester.calc( testFile );
            String correctHash = FileUtils.fileRead( Utils.SAMPLE_FILES_HASHCODES_PATH + File.separator
                                                     + testFile.getName() + digester.getFilenameExtension() );
            Assert.assertEquals( "The calculated " + digester.getAlgorithm() + " hashcode for "
                                 + testFile.getName() + " is incorrect.", correctHash, calculatedHash );
        }
    }

    /**
     * Check an exception is thrown when attempting to call {@link Digester#calc(File)} on a file that does not exist.
     *
     * @throws DigesterException should always happen.
     * @see Digester#calc(java.io.File)
     */
    @Test
    public void testCalcExceptionThrownOnFileNotFound() throws DigesterException
    {
        exception.expect( DigesterException.class );
        digester.calc( new File( "some/path/that/does/not/exist" ) );
    }

    /**
     * Check the {@link Digester#verify(java.io.File, String)} method response is valid against a pre-calculated
     * checksum.
     *
     * @throws DigesterException should never happen.
     * @throws IOException       should never happen.
     * @see Digester#verify(java.io.File, String)
     */
    @Test
    public void testVerify() throws DigesterException, IOException
    {
        List<File> testFiles = FileUtils.getFiles( new File( Utils.SAMPLE_FILES_PATH ), null, null );
        for ( File testFile : testFiles )
        {
            String correctHash = FileUtils.fileRead( Utils.SAMPLE_FILES_HASHCODES_PATH + File.separator
                                                     + testFile.getName() + digester.getFilenameExtension() );
            digester.verify( testFile, correctHash );
        }
    }

    /**
     * Check an exception is thrown when attempting to call {@link Digester#verify(File, String)} on a file that does
     * not exist.
     *
     * @throws DigesterException should always happen.
     * @see Digester#verify(java.io.File, String)
     */
    @Test
    public void testVerifyExceptionThrownOnFileNotFound() throws DigesterException
    {
        exception.expect( DigesterException.class );
        digester.verify( new File( "some/path/that/does/not/exist" ), "this is not a hashcode" );
    }
}
